package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/admin/comments/{commentId}")
@RequiredArgsConstructor
public class AdminCommentController {
    private final CommentService commentService;

    @GetMapping
    public CommentDto getCommentById(@PathVariable @Positive Long commentId) {
        return commentService.getCommentById(commentId);
    }

    @PatchMapping
    public CommentDto updateComment(@PathVariable @Positive Long commentId,
                                    @Valid @RequestBody NewCommentDto newCommentDto) {
        return commentService.updateCommentByAdmin(commentId, newCommentDto);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable @Positive Long commentId) {
        commentService.deleteCommentById(commentId);
    }
}
