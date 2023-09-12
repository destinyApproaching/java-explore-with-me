package ru.practicum.event.dto;

import lombok.experimental.UtilityClass;

import ru.practicum.category.dto.CategoryMapper;
import ru.practicum.event.entity.Event;
import ru.practicum.location.dto.LocationMapper;
import ru.practicum.user.dto.UserMapper;

@UtilityClass
public class EventMapper {
    public Event toEvent(NewEventDto newEventDto) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .title(newEventDto.getTitle())
                .eventDate(newEventDto.getEventDate())
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .build();
    }

    public EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(LocationMapper.toLocationDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .confirmedRequests(event.getConfirmedRequests())
                .views(event.getViews())
                .build();
    }

    public EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

//    public EventFullDto toEventFullDto(Event event) {
//        EventFullDto eventFullDto = EventFullDto.builder()
//                .annotation(event.getAnnotation())
//                .category(ru.practicum.main_service.categories.dto.CategoryMapper.toCategoryDto(event.getCategory()))
//                .createdOn(event.getCreatedOn())
//                .description(event.getDescription())
//                .eventDate(event.getEventDate())
//                .id(event.getId())
//                .initiator(UserMapper.toUserDto(event.getInitiator()))
//                .paid(event.getPaid())
//                .participantLimit(event.getParticipantLimit())
//                .publishedOn(event.getPublishedOn())
//                .requestModeration(event.getRequestModeration())
//                .state(event.getState())
//                .title(event.getTitle())
//                .location(LocationMapper.toLocationDto(event.getLocation()))
//                .build();
//        if (event.getParticipationRequests() != null && !event.getParticipationRequests().isEmpty()) {
//            eventFullDto.setConfirmedRequests(event.getParticipationRequests().stream()
//                    .filter(participationRequest -> participationRequest.getStatus() == Status.CONFIRMED)
//                    .count());
//        } else eventFullDto.setConfirmedRequests(0L);
//        return eventFullDto;
//    }
//
//    public NewEventDto toNewEventDtoDto(Event event) {
//        return NewEventDto.builder()
//                .annotation(event.getAnnotation())
//                .category(event.getCategory().getId())
//                .description(event.getDescription())
//                .eventDate(event.getEventDate())
//                .location(LocationMapper.toLocationDto(event.getLocation()))
//                .paid(event.getPaid())
//                .participantLimit(event.getParticipantLimit())
//                .requestModeration(event.getRequestModeration())
//                .title(event.getTitle())
//                .build();
//    }
//
//    public EventShortDto toEventShortDto(Event event, CategoryDto categoryDto, UserShortDto userDto) {
//        return EventShortDto.builder()
//                .id(event.getId())
//                .annotation(event.getAnnotation())
//                .category(categoryDto)
//                .confirmedRequests(event.getParticipantLimit())
//                .eventDate(event.getEventDate())
//                .initiator(userDto)
//                .paid(event.getPaid())
//                .title(event.getTitle())
//                .build();
//
//    }
//
//    public EventShortDto mapToShortDto(Event event) {
//        return EventShortDto.builder()
//                .annotation(event.getAnnotation())
//                .category(ru.practicum.main_service.categories.dto.CategoryMapper.toCategoryDto(event.getCategory()))
//                .eventDate(event.getEventDate())
//                .id(event.getId())
//                .initiator(UserMapper.toUserDto(event.getInitiator()))
//                .paid(event.getPaid())
//                .title(event.getTitle())
//                .build();
//    }
//
//    public List<EventShortDto> mapToShortDto(List<Event> events) {
//        return events.stream()
//                .map(EventMapper::mapToShortDto)
//                .collect(Collectors.toList());
//    }
//
//    public Event toEvent(NewEventDto newEventDto, ru.practicum.main_service.categories.model.Category category, Location location, User user) {
//        return Event.builder()
//                .annotation(newEventDto.getAnnotation())
//                .category(category)
//                .description(newEventDto.getDescription())
//                .eventDate(newEventDto.getEventDate())
//                .location(location)
//                .paid(newEventDto.getPaid())
//                .participantLimit(newEventDto.getParticipantLimit())
//                .requestModeration(newEventDto.getRequestModeration())
//                .title(newEventDto.getTitle())
//                .initiator(user)
//                .state(State.PENDING)
//                .build();
//    }
//
//    public EventFullDto mapToFullDto(Event event) {
//        EventFullDto dto = new EventFullDto();
//        dto.setAnnotation(event.getAnnotation());
//        dto.setCategory(ru.practicum.main_service.categories.dto.CategoryMapper.toCategoryDto(event.getCategory()));
//        dto.setCreatedOn(event.getCreatedOn());
//        dto.setDescription(event.getDescription());
//        dto.setEventDate(event.getEventDate());
//        dto.setId(event.getId());
//        dto.setInitiator(UserMapper.toUserDto(event.getInitiator()));
//        dto.setLocation(LocationMapper.toLocationDto(event.getLocation()));
//        dto.setPaid(event.getPaid());
//        dto.setParticipantLimit(event.getParticipantLimit());
//        dto.setPublishedOn(event.getPublishedOn());
//        dto.setRequestModeration(event.getRequestModeration());
//        dto.setState(event.getState());
//        dto.setTitle(event.getTitle());
//        dto.setConfirmedRequests(event.getConfirmedRequests());
//        dto.setViews(event.getViews());
//        return dto;
//    }
//
//    public List<EventFullDto> mapToFullDto(Iterable<Event> events) {
//        List<EventFullDto> result = new ArrayList<>();
//        for (Event event : events) {
//            result.add(mapToFullDto(event));
//        }
//        return result;
//    }
}

