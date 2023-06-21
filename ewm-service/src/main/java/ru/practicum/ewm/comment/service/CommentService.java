package ru.practicum.ewm.comment.service;

import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.model.dto.NewCommentDto;
import ru.practicum.ewm.comment.model.dto.OutputCommentDto;

import java.util.List;

public interface CommentService {

    /**
     * Method to get comment entity by id mandatory
     *
     * @param eventId required comment id
     * @return comment entity
     */
    Comment getCommentEntityByIdMandatory(long eventId);

    /**
     * Method to save comment
     *
     * @param newCommentDto new comment
     * @return posted comment
     */
    OutputCommentDto postComment(NewCommentDto newCommentDto);


    /**
     * Method to get event comments
     *
     * @param eventId event id
     * @param from first comment in result
     * @param size page size
     * @param sort sort by date ASC or DESC
     * @return comments to event
     */
    List<OutputCommentDto> getCommentsByEvent(long eventId, int from, int size, String sort);


    /**
     * Method to edit comment
     *
     * @param newCommentDto new text to comment
     * @return edited comment
     */
    OutputCommentDto patchComment(NewCommentDto newCommentDto);

    /**
     * Method to delete comment by id
     *
     * @param userId comment owner id
     * @param eventId commented event
     * @param commentId comment id
     */
    void deleteComment(long userId, long eventId, long commentId);
}
