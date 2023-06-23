package ru.practicum.ewm.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.model.dto.CommentMapper;
import ru.practicum.ewm.comment.model.dto.NewCommentDto;
import ru.practicum.ewm.comment.model.dto.OutputCommentDto;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.exception.InvalidResourceException;
import ru.practicum.ewm.exception.ResourceNotFoundException;
import ru.practicum.ewm.user.model.EwmUser;
import ru.practicum.ewm.user.service.EwmUserService;
import ru.practicum.ewm.util.OffsetPageRequest;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final EwmUserService ewmUserService;

    private final EventService eventService;

    private final CommentMapper commentMapper;

    @Override
    public Comment getCommentEntityByIdMandatory(long id) {
        log.info("[Comment Service] received a request to get comment entity  with id = '{}' mandatory", id);
        return commentRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(
                        String.format("Comment with id = '%s' not found", id)
                )
        );
    }

    @Override
    @Transactional
    public OutputCommentDto postComment(String eventIdString, String userIdString, NewCommentDto newCommentDto) {
        long userId = Long.parseLong(userIdString);
        long eventId = Long.parseLong(eventIdString);
        newCommentDto.setCommentator(userId);
        newCommentDto.setCommentedEvent(eventId);
        log.info("[Comment Service] received a request to post comment");
        EwmUser commentator = ewmUserService.getEwmUserEntityByIdMandatory(newCommentDto.getCommentator());
        Event commentedEvent = eventService.getEventEntityByIdMandatory(newCommentDto.getCommentedEvent());
        Comment commentToSave = commentMapper.newCommentDtoToComment(newCommentDto, commentator, commentedEvent, false);
        Comment savedComment = commentRepository.save(commentToSave);
        return commentMapper.commentToOutputCommentDto(savedComment, savedComment.getCommentator().getName());
    }

    @Override
    public List<OutputCommentDto> getCommentsByEvent(String eventIdString, int from, int size, String sort) {
        log.info("[Comment Service] received a request to get comments to event with id = '{}' " +
                "from = '{}' with size = '{}' and sort = ''{}", eventIdString, from, size, sort);
        long eventId = Long.parseLong(eventIdString);
        eventService.checkEventExistenceById(eventId);
        Sort byDate = Sort.by(Sort.Direction.DESC, "commentedOn");
        if (sort.equalsIgnoreCase("ASC")) {
            byDate = Sort.by(Sort.Direction.ASC);
        }

        Sort byDateAndById = Sort.by(Sort.Direction.DESC, "id").and(byDate);
        OffsetPageRequest pageRequest = OffsetPageRequest.of(from, size, byDateAndById);
        List<Comment> eventComments = commentRepository.findAllBy(eventId, pageRequest);
        return eventComments.stream()
                .map(comment ->
                        commentMapper.commentToOutputCommentDto(comment, comment.getCommentator().getName())
                ).collect(Collectors.toList());
    }


    @Override
    @Transactional
    public OutputCommentDto patchComment(String eventIdString, String userIdString, String commentIdString, NewCommentDto newCommentDto) {
        log.info("[Comment Service] received a request to patch comment");
        long userId = Long.parseLong(userIdString);
        long eventId = Long.parseLong(eventIdString);
        long commentId = Long.parseLong(commentIdString);
        newCommentDto.setId(commentId);
        newCommentDto.setCommentator(userId);
        newCommentDto.setCommentedEvent(eventId);
        ewmUserService.checkUserExistence(newCommentDto.getCommentator());
        eventService.checkEventExistenceById(newCommentDto.getCommentedEvent());
        Comment commentToUpdate = getCommentEntityByIdMandatory(newCommentDto.getId());
        if (newCommentDto.getCommentator() != commentToUpdate.getCommentator().getId()) {
            throw new InvalidResourceException(
                    String.format("user with id = '%s' is not comment owner", newCommentDto.getCommentator())
            );
        }

        if (newCommentDto.getCommentedEvent() != commentToUpdate.getCommentedEvent().getId()) {
            throw new InvalidResourceException(
                    String.format("event with id = '%s' is not comment event", newCommentDto.getCommentedEvent())
            );
        }

        commentToUpdate.setText(newCommentDto.getText());
        commentToUpdate.setEdited(true);
        commentToUpdate.setCommentedOn(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        return commentMapper.commentToOutputCommentDto(commentRepository.save(commentToUpdate),
                commentToUpdate.getCommentator().getName());
    }

    @Override
    @Transactional
    public void deleteComment(String userIdString, String eventIdString, String commentIdString) {
        long userId = Long.parseLong(userIdString);
        long eventId = Long.parseLong(eventIdString);
        long commentId = Long.parseLong(commentIdString);
        log.info("[Comment Service] received a request to patch comment");
        ewmUserService.checkUserExistence(userId);
        eventService.checkEventExistenceById(eventId);
        Comment commentToDelete = getCommentEntityByIdMandatory(commentId);
        if (userId != commentToDelete.getCommentator().getId()) {
            throw new InvalidResourceException(
                    String.format("user with id = '%s' is not comment owner", userId)
            );
        }

        if (eventId != commentToDelete.getCommentedEvent().getId()) {
            throw new InvalidResourceException(
                    String.format("event with id = '%s' is not comment event", eventId)
            );
        }

        commentRepository.deleteById(commentId);

    }
}
