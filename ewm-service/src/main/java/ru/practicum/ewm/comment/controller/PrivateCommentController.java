package ru.practicum.ewm.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.comment.model.dto.NewCommentDto;
import ru.practicum.ewm.comment.model.dto.OutputCommentDto;
import ru.practicum.ewm.comment.service.CommentService;


import javax.validation.Valid;

@RestController
@RequestMapping("/users/{userIdString}/events/{eventIdString}/comments")
@Slf4j
@RequiredArgsConstructor
public class PrivateCommentController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OutputCommentDto postCommentByUser(
            @PathVariable String eventIdString,
            @PathVariable String userIdString,
            @RequestBody @Valid NewCommentDto newCommentDto) {
        long userId = Long.parseLong(userIdString);
        long eventId = Long.parseLong(eventIdString);
        newCommentDto.setCommentator(userId);
        newCommentDto.setCommentedEvent(eventId);
        log.info("[Private Comment Controller] received a request POST /users/{}/events/{}/comments",
                userIdString,
                eventIdString);
        return commentService.postComment(newCommentDto);
    }


    @PatchMapping("/{commentIdString}")
    public OutputCommentDto patchCommentByUser(
            @PathVariable String eventIdString,
            @PathVariable String userIdString,
            @RequestBody @Valid NewCommentDto newCommentDto,
            @PathVariable String commentIdString) {
        long userId = Long.parseLong(userIdString);
        long eventId = Long.parseLong(eventIdString);
        long commentId = Long.parseLong(commentIdString);
        newCommentDto.setId(commentId);
        newCommentDto.setCommentator(userId);
        newCommentDto.setCommentedEvent(eventId);
        log.info("[Private Comment Controller] received a request PATCH /users/{}/events/{}/comments",
                userIdString,
                eventIdString);
        return commentService.patchComment(newCommentDto);
    }

    @DeleteMapping("/{commentIdString}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void patchCommentByUser(
            @PathVariable String eventIdString,
            @PathVariable String userIdString,
            @PathVariable String commentIdString) {
        long userId = Long.parseLong(userIdString);
        long eventId = Long.parseLong(eventIdString);
        long commentId = Long.parseLong(commentIdString);
        log.info("[Private Comment Controller] received a request DELETE /users/{}/events/{}/comments",
                userIdString,
                eventIdString);
        commentService.deleteComment(userId, eventId, commentId);
    }
}
