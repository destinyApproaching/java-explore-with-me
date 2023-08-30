package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    public EndpointHitDto saveHit(@RequestBody @Valid EndpointHitDto endpointHitDto) {
        return statsService.saveHit(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@RequestParam("start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                       @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                       @RequestParam(name = "uris", required = false, defaultValue = "") List<String> uris,
                                       @RequestParam(name = "unique", required = false, defaultValue = "false") Boolean unique) {
        return statsService.getStats(start, end, uris, unique);
    }
}
