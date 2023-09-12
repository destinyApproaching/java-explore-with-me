package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.pattern.DateTimePattern;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StatsService {
    private static final String APP = "main";
    private final StatsClient statsClient;

    public ResponseEntity<EndpointHitDto> saveHit(String uri, String ip) {
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app(APP)
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build();
        return statsClient.postHit(endpointHitDto);
    }

    public Long getView(Long eventId) {
        List<String> uris = List.of("/events/" + eventId);
        ResponseEntity<List<ViewStatsDto>> entity = statsClient.getStats(
                LocalDateTime.of(2000, 1, 1, 1, 1).format(DateTimeFormatter.ofPattern(DateTimePattern.PATTERN)),
                LocalDateTime.of(2100, 1, 1, 1, 1).format(DateTimeFormatter.ofPattern(DateTimePattern.PATTERN)),
                uris,
                true);

        if (entity.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Получить статистику не удалось");
        }

        if (Objects.requireNonNull(entity.getBody()).isEmpty()) {
            return 0L;
        }
        return entity.getBody().stream().mapToLong(ViewStatsDto::getHits).sum();

    }

//    public EventFullDto setViewsNumber(EventFullDto event) {
//        List<StatsResponseDto> hits = statsClient.getStatistic(event.getCreatedOn(), LocalDateTime.now(),
//                List.of("/events/" + event.getId()), true);
//        if (!hits.isEmpty()) {
//            event.setViews(hits.get(0).getHits());
//        } else {
//            event.setViews(0L);
//        }
//        return event;
//    }
//
//    public List<EventShortDto> setViewsNumber(List<EventShortDto> events) {
//        List<String> uris = new ArrayList<>();
//        for (EventShortDto eventShortDto : events) {
//            uris.add("/events/" + eventShortDto.getId());
//        }
//
//        List<StatsResponseDto> hits = statsClient.getStatistic(LocalDateTime.now().minusYears(YEARS_OFFSET),
//                LocalDateTime.now(), uris, true);
//        if (!hits.isEmpty()) {
//            Map<Long, Integer> hitMap = mapHits(hits);
//            for (EventShortDto event : events) {
//                event.setViews(hitMap.getOrDefault(event.getId(), 0));
//            }
//        } else {
//            for (EventShortDto event : events) {
//                event.setViews(0);
//            }
//        }
//        return events;
//    }
//
//    private Map<Long, Integer> mapHits(List<ViewStatsDto> hits) {
//        Map<Long, Integer> hitMap = new HashMap<>();
//        for (var hit : hits) {
//            String hitUri = hit.getUri();
//            Long id = Long.valueOf(hitUri.substring(8));
//            hitMap.put(id, (int) hit.getHits());
//        }
//        return hitMap;
//    }
}
