package ru.practicum.event.service;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventRequestDto;
import ru.practicum.event.enums.CustomSort;
import ru.practicum.event.enums.State;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventShortDto> getEvents(String text,
                                  List<Long> categories,
                                  Boolean paid,
                                  LocalDateTime rangeStart,
                                  LocalDateTime rangeEnd,
                                  Boolean onlyAvailable,
                                  CustomSort sort,
                                  Integer from,
                                  Integer size,
                                  HttpServletRequest request);

    EventFullDto getEventById(Long eventId,
                              HttpServletRequest request);

    List<EventFullDto> getAllEventsByUserId(Long userId,
                                            Integer from,
                                            Integer size);

    EventFullDto createEvents(Long userId,
                              NewEventDto newEventDto);

    EventFullDto getEventsByUserId(Long userId,
                                   Long eventId);

    EventFullDto updateEventsByUser(Long userId,
                                    Long eventId,
                                    UpdateEventRequestDto requestDto);

    List<ParticipationRequestDto> getRequestUserEvents(Long userId,
                                                       Long eventId);

    EventRequestStatusUpdateResult updateStatusRequestByUserId(Long userId,
                                                               Long eventId,
                                                               EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    List<EventFullDto> getEventsToAdmin(List<Long> userIds,
                                      List<State> states,
                                      List<Long> categories,
                                      LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd,
                                      Integer from,
                                      Integer size);

    EventFullDto updateEventsByAdmin(Long eventId,
                                     UpdateEventRequestDto updateEventRequestDto);

//    List<EventShortDto> getEventsListInLocation(Long locationId, Float lat, Float lon,
//                                            Float radius, Integer from, Integer size);
}
