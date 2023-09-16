package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.*;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getEventsByUserId(@PathVariable Long userId,
                                                   @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                                   @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        return eventService.getEventsByUserId(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId,
                                     @RequestBody @Valid NewEventDto newEventDto) {
        return eventService.createEvents(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventByUserId(@PathVariable Long userId,
                                          @PathVariable Long eventId) {
        return eventService.getEventByUserId(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByUser(@PathVariable Long userId,
                                           @PathVariable Long eventId,
                                           @RequestBody @Valid UpdateEventRequestDto requestDto) {
        return eventService.updateEventsByUser(userId, eventId, requestDto);
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
        return eventService.updateStatusRequestByUserIdForEvents(userId, eventId, requestDto);
    }
}
