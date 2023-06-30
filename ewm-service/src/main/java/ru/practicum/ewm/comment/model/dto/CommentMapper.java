package ru.practicum.ewm.comment.model.dto;

import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.EwmUser;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring",
        imports = LocalDateTime.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "commentator", source = "user")
    @Mapping(target = "commentedEvent", source = "event")
    @Mapping(target = "commentedOn", expression = "java(LocalDateTime.now())")
    @Mapping(target = "edited", source = "edited")
    Comment newCommentDtoToComment(NewCommentDto newCommentDto, EwmUser user, Event event, boolean edited);

    @Mapping(target = "id", source = "comment.id")
    @Mapping(target = "commentatorName", source = "commentatorName")
    OutputCommentDto commentToOutputCommentDto(Comment comment, String commentatorName);
}
