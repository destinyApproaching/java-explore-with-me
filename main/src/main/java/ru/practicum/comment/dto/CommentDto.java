package ru.practicum.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.pattern.DateTimePattern;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;
    private Long author;
    private Long event;
    private String description;
    @JsonFormat(pattern = DateTimePattern.PATTERN)
    private LocalDateTime timestamp;
}
