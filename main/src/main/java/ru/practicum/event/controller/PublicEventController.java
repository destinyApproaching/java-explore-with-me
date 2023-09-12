package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.enums.CustomSort;

import ru.practicum.event.service.EventService;
import ru.practicum.pattern.DateTimePattern;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
public class PublicEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                                 @RequestParam(required = false) List<Long> categories,
                                                 @RequestParam(required = false) Boolean paid,
                                                 @RequestParam(required = false) @DateTimeFormat(pattern = DateTimePattern.PATTERN) LocalDateTime rangeStart,
                                                 @RequestParam(required = false) @DateTimeFormat(pattern = DateTimePattern.PATTERN) LocalDateTime rangeEnd,
                                                 @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
                                                 @RequestParam(required = false, defaultValue = "EVENT_DATE") CustomSort sort,
                                                 @RequestParam(required = false, defaultValue = "0") Integer from,
                                                 @RequestParam(required = false, defaultValue = "10") Integer size,
                                                 HttpServletRequest request) {

//        LocalDateTime start = null;
//        LocalDateTime end = null;
//        if (rangeStart != null) {
//            start = LocalDateTime.parse(rangeStart, dateTimeFormatter);
//        }
//        if (rangeEnd != null) {
//            end = LocalDateTime.parse(rangeEnd, dateTimeFormatter);
//        }
//
//        if (start != null && end != null) {
//            if (start.isAfter(end)) {
//                log.info("Start date {} is after end date {}.", start, end);
//                throw new ValidationException(String.format("Start date %s is after end date %s.", start, end));
//            }
//        }

        return eventService.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/{id}")
    public EventFullDto getEventById(@PathVariable Long id, HttpServletRequest request) {

        return eventService.getEventById(id, request);
    }

//    @GetMapping("/locations")
//    public List<EventShortDto> getEventsListInLocation(
//            @RequestParam(required = false) @Positive Long locationId,
//            @RequestParam(required = false) Float lat,
//            @RequestParam(required = false) Float lon,
//            @RequestParam(defaultValue = "0.0") Float radius,
//            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
//            @RequestParam(defaultValue = "10") @Positive Integer size) {
//        List<EventShortDto> result = eventService.getEventsListInLocation(
//                locationId, lat, lon, radius, from, size);
//        return result;
//    }
}
