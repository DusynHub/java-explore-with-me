package ru.practicum.ewm.compilation.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.model.QCompilation;
import ru.practicum.ewm.compilation.model.dto.CompilationDto;
import ru.practicum.ewm.compilation.model.dto.CompilationMapper;
import ru.practicum.ewm.compilation.model.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.model.dto.PatchCompilationDto;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.QEvent;
import ru.practicum.ewm.event.model.dto.EventShortDto;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.exception.ResourceNotFoundException;
import ru.practicum.ewm.util.OffsetPageRequest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final EventService eventService;

    private final CompilationRepository compilationRepository;

    @Override
    @Transactional
    public CompilationDto postCompilation(NewCompilationDto newCompilationDto) {
        log.info("[Compilation Service] received an admin request POST /admin/compilations");

        if (newCompilationDto.getPinned() == null) {
            newCompilationDto.setPinned(false);
        }

        List<Event> eventsToCompilation = new ArrayList<>(0);
        if(newCompilationDto.getEvents() != null ){
            eventsToCompilation = eventService.getEventsById(newCompilationDto.getEvents());
        }

        Set<Event> eventsSetToCompilation = new HashSet<>(
                eventsToCompilation);

        Compilation compilationToSave = CompilationMapper.INSTANCE.newCompilationDtoToCompilation(
                newCompilationDto,
                eventsSetToCompilation);

        List<EventShortDto> shortEvents = eventService.makeEvenShortDtoFromEventsList(eventsToCompilation)
                .stream()
                .sorted(Comparator.comparing(EventShortDto::getId))
                .collect(Collectors.toList());

        Compilation savedCompilation = compilationRepository.save(compilationToSave);

        return CompilationMapper.INSTANCE.compilationToCompilationDto(
                savedCompilation, shortEvents);
    }

    @Override
    @Transactional
    public void deleteCompilationById(long compilationId) {
        log.info("[Compilation Service] received an admin request " +
                "to delete compilation with id = '{}'", compilationId);
        checkCompilationExistenceById(compilationId);
        compilationRepository.deleteById(compilationId);
    }

    @Override
    @Transactional
    public CompilationDto patchCompilationById(long compilationId,
                                               PatchCompilationDto patchCompilationDto) {
        log.info("[Compilation Service] received an admin request PATCH /admin/compilations/{}",
                compilationId);
        checkCompilationExistenceById(compilationId);
        Compilation compilationToBePatched = compilationRepository.findById(compilationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                String.format("Compilation with id = '%s' not found", compilationId))
                );

        Set<Long> newEventIdsSetToCompilationInSet = patchCompilationDto.getEvents();
        List<Event> newEventsInList = new ArrayList<>(0);
        Set<Event> newEventSetToCompilation = null;

        if (newEventIdsSetToCompilationInSet != null) {
            newEventsInList = eventService.getEventsById(newEventIdsSetToCompilationInSet);
            newEventSetToCompilation = new HashSet<>(newEventsInList);
        } else {
            newEventsInList = new ArrayList<>(compilationToBePatched.getEvents());
        }

        CompilationMapper.INSTANCE.patchCompilationDtoToCompilation(
                patchCompilationDto,
                newEventSetToCompilation,
                compilationToBePatched);

        Compilation pathchedCompilation = compilationRepository.save(compilationToBePatched);
        List<EventShortDto> shortEvents = eventService.makeEvenShortDtoFromEventsList(newEventsInList);

        return CompilationMapper.INSTANCE.compilationToCompilationDto(
                pathchedCompilation, shortEvents);
    }

    @Override
    public List<CompilationDto> getCompilationsFromUser(Boolean pinned, int from, int size) {
        log.info("[Compilation Service] received a public request to get compilations");
        BooleanExpression expression = Expressions.asBoolean(true).eq(true);

        if (pinned != null) {
            expression = expression.and(QCompilation.compilation.pinned.eq(pinned));
        }

        Sort getCompilationsSortById = Sort.by(Sort.Direction.ASC, "id");
        OffsetPageRequest pageRequest = OffsetPageRequest.of(from, size, getCompilationsSortById);
        List<Compilation> requiredCompilations = compilationRepository
                .findAll(expression, pageRequest).getContent();
        Map<Long, List<EventShortDto>> shortEventsToCompilation
                = requiredCompilations.stream()
                .collect(Collectors.toMap(Compilation::getId,
                        comp -> eventService
                                .makeEvenShortDtoFromEventsList(new ArrayList<>(comp.getEvents()))));
        return requiredCompilations.stream().map(comp ->
                        CompilationMapper.INSTANCE
                                .compilationToCompilationDto(comp, shortEventsToCompilation.get(comp.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(long compilationId) {
        log.info("[Public Compilation Controller] received a public " +
                "request to get compilation with id = '{}'", compilationId);
        checkCompilationExistenceById(compilationId);
        Compilation requiredCompilation = compilationRepository.findById(compilationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                String.format("Compilation with id = '%s' not found", compilationId)));

        List<EventShortDto> shortEvents = eventService.makeEvenShortDtoFromEventsList(
                        new ArrayList<>(requiredCompilation.getEvents())
                ).stream()
                .sorted(Comparator.comparing(EventShortDto::getId))
                .collect(Collectors.toList());

        return CompilationMapper.INSTANCE.compilationToCompilationDto(
                requiredCompilation,
                shortEvents
        );
    }

    private void checkCompilationExistenceById(long compilationId) {
        if (!compilationRepository.existsById(compilationId)) {
            throw new ResourceNotFoundException(
                    String.format("Compilation with id = '%s' not found", compilationId));
        }
    }


}
