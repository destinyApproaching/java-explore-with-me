package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventRequestDto;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getAllEventsByUserId(@PathVariable Long userId,
                                                   @RequestParam(required = false, defaultValue = "0")
                                                   @PositiveOrZero Integer from,
                                                   @RequestParam(required = false, defaultValue = "10")
                                                   @PositiveOrZero Integer size) {
        return eventService.getAllEventsByUserId(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvents(@PathVariable Long userId,
                                     @RequestBody @Valid NewEventDto newEventDto) {
        return eventService.createEvents(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventsByUserId(@PathVariable Long userId,
                                          @PathVariable Long eventId) {
        return eventService.getEventsByUserId(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventsByUser(@PathVariable Long userId,
                                           @PathVariable Long eventId,
                                           @RequestBody @Valid UpdateEventRequestDto updateEventRequestDto) {
        return eventService.updateEventsByUser(userId, eventId, updateEventRequestDto);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestUserEvents(@PathVariable Long userId,
                                                              @PathVariable Long eventId) {
        return eventService.getRequestUserEvents(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateStatusRequestByUserIdForEvents(@PathVariable Long userId,
                                                                               @PathVariable Long eventId,
                                                                               @RequestBody @Valid EventRequestStatusUpdateRequest requestDto) {
        return eventService.updateStatusRequestByUserId(userId, eventId, requestDto);
    }
}
