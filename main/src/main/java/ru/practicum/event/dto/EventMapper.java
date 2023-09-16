package ru.practicum.event.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.category.dto.CategoryMapper;
import ru.practicum.category.entity.Category;
import ru.practicum.event.entity.Event;
import ru.practicum.event.entity.Location;
import ru.practicum.event.enums.State;
import ru.practicum.request.enums.Status;
import ru.practicum.users.dto.UserMapper;
import ru.practicum.users.entity.User;

@UtilityClass
public class EventMapper {

    public EventFullDto toEventFullDto(Event event) {
        EventFullDto eventFullDto = EventFullDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(UserMapper.toUserDto(event.getInitiator()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .location(LocationMapper.toLocationDto(event.getLocation()))
                .build();
        if (event.getParticipationRequests() != null && !event.getParticipationRequests().isEmpty()) {
            eventFullDto.setConfirmedRequests(event.getParticipationRequests().stream()
                             .filter(participationRequest -> participationRequest.getStatus() == Status.CONFIRMED)
                    .count());
        } else eventFullDto.setConfirmedRequests(0L);
        return eventFullDto;
    }

    public EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getParticipantLimit())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .build();
    }

    public Event toEvent(NewEventDto newEventDto, Category categories, Location location, User user) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .category(categories)
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .location(location)
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .title(newEventDto.getTitle())
                .initiator(user)
                .state(State.PENDING)
                .build();
    }
}

