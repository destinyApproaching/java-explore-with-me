package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentMapper;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.entity.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.entity.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.users.entity.User;
import ru.practicum.users.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public CommentDto addComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User user = getUser(userId);
        Event event = getEvent(eventId);
        return CommentMapper
                .toCommentDto(commentRepository.save(CommentMapper.toComment(newCommentDto, user, event)));
    }

    @Override
    public CommentDto updateComment(Long userId, Long eventId, Long commentId, NewCommentDto newCommentDto) {
        Comment comment = checkUserEventComment(userId, eventId, commentId);
        comment.setDescription(newCommentDto.getDescription());
        comment.setTimestamp(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto updateCommentByAdmin(Long commentId, NewCommentDto newCommentDto) {
        Comment comment = getComment(commentId);
        comment.setDescription(newCommentDto.getDescription());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto getCommentById(Long commentId) {
        return CommentMapper.toCommentDto(getComment(commentId));
    }

    @Override
    public void deleteCommentByUser(Long userId, Long eventId, Long commendId) {
        checkUserEventComment(userId, eventId, commendId);
        commentRepository.deleteById(commendId);
    }

    private Comment checkUserEventComment(Long userId, Long eventId, Long commendId) {
        User user = getUser(userId);
        Event event = getEvent(eventId);
        Comment comment = getComment(commendId);
        if (!comment.getAuthor().equals(user) || !comment.getEvent().equals(event)) {
            throw new BadRequestException("Нельзя изменять чужие комментарии");
        }
        return comment;
    }

    @Override
    public void deleteCommentById(Long commentId) {
        getComment(commentId);
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentDto> getCommentsOnEvent(Long eventId, int from, int size) {
        return commentRepository
                .findAllByEventIdOrderByTimestamp(eventId, PageRequest.of(from == 0 ? 0 : from / size, size))
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getUserComments(Long userId, int from, int size) {
        return commentRepository
                .findAllByAuthorIdOrderByTimestamp(userId, PageRequest.of(from == 0 ? 0 : from / size, size))
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие не найдено"));
    }

    private Comment getComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Комментарий не найден"));
    }
}