package ru.practicum.ewm.compilation.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.model.dto.CompilationDto;
import ru.practicum.ewm.compilation.model.dto.CompilationMapper;
import ru.practicum.ewm.compilation.model.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.dto.EventShortDto;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.service.EventService;

import java.util.HashSet;
import java.util.List;
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
        log.info("[Compilation Service] received a request POST /admin/compilations");

        if (newCompilationDto.getPinned() == null) {
            newCompilationDto.setPinned(false);
        }

        List<Event> eventsToCompilation = eventService.getEventsById(newCompilationDto.getEvents());
        Set<Event> eventsSetToCompilation = new HashSet<>(
                eventsToCompilation);

        Compilation compilationToSave = CompilationMapper.INSTANCE.newCompilationDtoToCompilation(
                newCompilationDto,
                eventsSetToCompilation);

        List<EventShortDto> shortEvents = eventService.makeEvenShortDtoFromEventsList(eventsToCompilation);
        Compilation savedCompilation = compilationRepository.save(compilationToSave);

        return  CompilationMapper.INSTANCE.compilationToCompilationDto(
                savedCompilation, shortEvents);
    }



}
