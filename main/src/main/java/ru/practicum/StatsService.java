package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.exception.BadRequestException;
import ru.practicum.pattern.DateTimePattern;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {
    private final StatsClient statsClient;
    private static final String APP = "main";
    private static final String URI_PREFIX = "/events";

    public void hitEvent(Long eventId) {
        String uri = URI_PREFIX + (eventId == null ? "" : "/" + eventId);
        statsClient.hitStat(EndpointHitDto.builder()
                .app(APP)
                .ip("127.0.0.1")
                .uri(uri)
                .build());
    }


    public Long getView(Long eventId) {
        List<String> uris = List.of("/events/" + eventId);
        ResponseEntity<List<ViewStatsDto>> entity = statsClient.getStat(
                LocalDateTime.of(2000, 1, 1, 1, 1).format(DateTimeFormatter.ofPattern(DateTimePattern.PATTERN)),
                LocalDateTime.of(4000, 1, 1, 1, 1).format(DateTimeFormatter.ofPattern(DateTimePattern.PATTERN)),
                uris,
                true);

        if (entity.getStatusCode() != HttpStatus.OK) {
            throw new BadRequestException("Получить статистику не удалось");
        }

        if (entity.getBody().isEmpty()) {
            return 0L;
        }
        return entity.getBody().stream().mapToLong(ViewStatsDto::getHits).sum();
    }
}
