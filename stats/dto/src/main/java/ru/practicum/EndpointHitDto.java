package ru.practicum;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.pattern.DateTimePattern;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EndpointHitDto {
    @NotBlank(message = "Отсутсвует app.")
    private String app;
    @NotBlank(message = "Отсутствует uri.")
    private String uri;
    @NotBlank(message = "Отсутсвует ip.")
    private String ip;
    @NotNull(message = "Отсутствует время.")
    @JsonFormat(pattern = DateTimePattern.PATTERN)
    private LocalDateTime timestamp;
}
