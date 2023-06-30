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
    OutputCommentDto postComment(String eventIdString, String userIdString, NewCommentDto newCommentDto);


    /**
     * Method to get event comments
     *
     * @param eventIdString event id
     * @param from first comment in result
     * @param size page size
     * @param sort sort by date ASC or DESC
     * @return comments to event
     */
    List<OutputCommentDto> getCommentsByEvent(String eventIdString, int from, int size, String sort);


    /**
     * Method to edit comment
     *
     * @param newCommentDto new text to comment
     * @return edited comment
     */
    OutputCommentDto patchComment(String eventIdString, String userIdString, String commentIdString, NewCommentDto newCommentDto);

    /**
     * Method to delete comment by id
     *
     * @param userIdString comment owner id
     * @param eventIdString commented event
     * @param commentIdString comment id
     */
    void deleteComment(String userIdString, String eventIdString, String commentIdString);
}
