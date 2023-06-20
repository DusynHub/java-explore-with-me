package ru.practicum.ewm.comment.model.dto;

import org.mapstruct.Mapper;

import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.comment.model.Comment;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring",
        imports = LocalDateTime.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@Service
public interface CommentMapper {

//    Comment commentDtoToComment(CommentDto commentDto);
//
//    CommentDto commentToCommentDto(Comment comment);
}
