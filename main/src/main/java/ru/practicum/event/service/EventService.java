package ru.practicum.event.service;

import ru.practicum.event.dto.*;
import ru.practicum.event.enums.CustomSort;
import ru.practicum.event.enums.State;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, CustomSort sort, Integer from, Integer size);

    EventFullDto getEventById(Long eventId);

    List<EventFullDto> getEventsByUserId(Long userId, Integer from, Integer size);

    EventFullDto createEvents(Long userId, NewEventDto newEventDto);

    EventFullDto getEventByUserId(Long userId, Long eventId);

    EventFullDto updateEventsByUser(Long userId, Long eventId, UpdateEventRequestDto requestDto);

    List<ParticipationRequestDto> getRequestUserEvents(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateStatusRequestByUserIdForEvents(Long userId, Long eventId, EventRequestStatusUpdateRequest requestDto);

    List<EventFullDto> getEventsByAdmin(List<Long> userIds, List<State> states, List<Long> categories, String rangeStart, String rangeEnd, Integer from, Integer size);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventRequestDto requestDto);
}
