package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.pattern.DateTimePattern;
import ru.practicum.service.StatsService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {
    private final StatsService service;

    @PostMapping("/hit")
    public ResponseEntity<EndpointHitDto> postHit(@RequestBody @Valid EndpointHitDto endpointHitDto, HttpServletRequest request) {
        endpointHitDto.setIp(request.getRemoteAddr());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.postHit(endpointHitDto));
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStatsDto>> getStats(@RequestParam @DateTimeFormat(pattern = DateTimePattern.PATTERN) LocalDateTime start,
                                                       @RequestParam @DateTimeFormat(pattern = DateTimePattern.PATTERN) LocalDateTime end,
                                                       @RequestParam(required = false) List<String> uris,
                                                       @RequestParam(defaultValue = "false") boolean unique) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getStats(start, end, uris, unique));
    }
}
