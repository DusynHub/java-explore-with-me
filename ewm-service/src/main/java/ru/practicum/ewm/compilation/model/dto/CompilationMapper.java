package ru.practicum.ewm.compilation.model.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.dto.EventMapper;
import ru.practicum.ewm.event.model.dto.EventShortDto;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CompilationMapper {

    CompilationMapper INSTANCE = Mappers.getMapper(CompilationMapper.class);


    @Mapping(target = "events",
            source = "events")
    Compilation newCompilationDtoToCompilation(NewCompilationDto newCompilationDto,
                                               Set<Event> events);


    @Mapping(target = "events",
            source = "events")
    CompilationDto compilationToCompilationDto(Compilation compilation,
                                               List<EventShortDto> events);
}