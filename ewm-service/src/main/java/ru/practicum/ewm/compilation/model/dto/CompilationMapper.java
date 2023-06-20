package ru.practicum.ewm.compilation.model.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.dto.EventShortDto;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@Service
public interface CompilationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events",
            source = "events")
    Compilation newCompilationDtoToCompilation(NewCompilationDto newCompilationDto,
                                               Set<Event> events);

    @Mapping(target = "events",
            source = "events")
    CompilationDto compilationToCompilationDto(Compilation compilation,
                                               List<EventShortDto> events);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events",
            source = "newEventSetToCompilation")
    void patchCompilationDtoToCompilation(
            PatchCompilationDto patchCompilationDto,
            Set<Event> newEventSetToCompilation,
            @MappingTarget Compilation compilationToBePatched);
}