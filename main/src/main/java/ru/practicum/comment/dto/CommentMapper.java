package ru.practicum.comment.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.comment.entity.Comment;
import ru.practicum.event.entity.Event;
import ru.practicum.users.entity.User;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {
    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .author(comment.getAuthor().getId())
                .event(comment.getEvent().getId())
                .description(comment.getDescription())
                .timestamp(comment.getTimestamp())
                .build();
    }

    public Comment toComment(NewCommentDto newCommentDto, User user, Event event) {
        return Comment.builder()
                .author(user)
                .event(event)
                .description(newCommentDto.getDescription())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
