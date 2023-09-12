package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventRequestDto;
import ru.practicum.event.enums.State;
import ru.practicum.event.service.EventService;
import ru.practicum.pattern.DateTimePattern;

import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class AdminEventController {
    private final EventService eventService;

    @GetMapping()
    public List<EventFullDto> adminGetEvents(@RequestParam(required = false) List<Long> users,
                                                 @RequestParam(required = false) List<State> states,
                                                 @RequestParam(required = false) List<Long> categories,
                                                 @RequestParam(required = false) @DateTimeFormat(pattern = DateTimePattern.PATTERN) LocalDateTime rangeStart,
                                                 @RequestParam(required = false) @DateTimeFormat(pattern = DateTimePattern.PATTERN) LocalDateTime rangeEnd,
                                                 @RequestParam(defaultValue = "0") Integer from,
                                                 @RequestParam(defaultValue = "10") Integer size) {
        return eventService.getEventsToAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto patchAdminEvent(@PathVariable @Positive Long eventId,
                                        @RequestBody @Validated UpdateEventRequestDto updateEventRequestDto) {
        return eventService.updateEventsByAdmin(eventId, updateEventRequestDto);
    }
}
