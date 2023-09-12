package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.StatsService;
import ru.practicum.category.entity.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.*;
import ru.practicum.event.entity.Event;
import ru.practicum.event.enums.CustomSort;
import ru.practicum.event.enums.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.dto.LocationMapper;
import ru.practicum.location.entity.Location;
import ru.practicum.location.repository.LocationRepository;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.RequestMapper;
import ru.practicum.request.entity.Request;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.request.enums.Status;
import ru.practicum.user.entity.User;
import ru.practicum.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final StatsService statsService;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    //    private final CustomBuiltEventRepository customBuiltEventRepository;
    private final LocationRepository locationRepository;

    @Override
    public EventFullDto createEvents(Long userId, NewEventDto newEventDto) {
        if (newEventDto.getRequestModeration() == null) {
            newEventDto.setRequestModeration(true);
        }
        LocalDateTime eventDate = newEventDto.getEventDate();
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Событие не может быть раньше, чем через два часа от текущего момента!");
        }
        Event event = EventMapper.toEvent(newEventDto);
        User user = getUser(userId);
        event.setInitiator(user);
        Category category = getCategory(newEventDto.getCategory());
        event.setCategory(category);
        Location location = getLocation(newEventDto.getLocation());
        event.setLocation(location);
        event.setConfirmedRequests(0L);
        event.setViews(0L);
        return EventMapper.toEventFullDto(event);

//        User user = getUser(userId);
//        Location location = getLocation(newEventDto.getLocation());
//        Category category = getCategory(newEventDto.getCategory());
//        Event event = EventMapper.toEvent(newEventDto, category, location, user);
//        event.setConfirmedRequests(0L);
//        event.setViews(0L);
//        Event result = eventRepository.save(event);
//        return EventMapper.toEventFullDto(result);
    }

    @Override
    public EventRequestStatusUpdateResult updateStatusRequestByUserId(Long userId,
                                                                      Long eventId,
                                                                      EventRequestStatusUpdateRequest requeseventRequestStatusUpdateRequestStatusUpdate) {
        User user = getUser(userId);
        Event event = getEvents(eventId);
        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException("Пользователь не инициатор события!");
        }
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new ConflictException("Не требуется модерация и подтверждения заявок");
        }
        Long confirmedRequests = requestRepository.countAllByEventIdAndStatus(eventId, Status.CONFIRMED);
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= (confirmedRequests)) {
            throw new ConflictException("Нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие!");
        }
        List<Request> confirmed = new ArrayList<>();
        List<Request> rejected = new ArrayList<>();
        for (Request request : requestRepository.findAllByIdIn(requeseventRequestStatusUpdateRequestStatusUpdate.getRequestIds())) {
            if (!request.getStatus().equals(Status.PENDING)) {
                continue;
            }
            if (!request.getEvent().getId().equals(eventId)) {
                rejected.add(request);
            }
            if (requeseventRequestStatusUpdateRequestStatusUpdate.getStatus().equals("CONFIRMED")) {
                if (confirmedRequests < event.getParticipantLimit()) {
                    request.setStatus(Status.CONFIRMED);
                    confirmedRequests++;
                    confirmed.add(request);
                } else {
                    request.setStatus(Status.REJECTED);
                    rejected.add(request);
                }
            } else {
                request.setStatus(Status.REJECTED);
                rejected.add(request);
            }
        }
//        eventRepository.save(event);
//        requestRepository.saveAll(requestsToUpdate);
        return RequestMapper.toUpdateResultDto(confirmed, rejected);
    }



    @Override
    public EventFullDto updateEventsByUser(Long userId, Long eventId, UpdateEventRequestDto updateEventRequestDto) {
        Event event = getEvents(eventId);
        if (event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Нельзя изменить опубликованное событие.");
        }
        updateEvent(event, updateEventRequestDto);
        if (updateEventRequestDto.getStateAction() != null) {
            switch (updateEventRequestDto.getStateAction()) {
                case CANCEL_REVIEW:
                    event.setState(State.CANCELED);
                    break;
                case SEND_TO_REVIEW:
                    event.setState(State.PENDING);
                    event.setPublishedOn(LocalDateTime.now());
            }
        }
        EventFullDto eventFullDto = EventMapper.toEventFullDto(eventRepository.save(event));
        eventFullDto.setConfirmedRequests(requestRepository.countAllByEventIdAndStatus(event.getId(), Status.CONFIRMED));
//        statsClient.setViewsNumber(eventFullDto);
        return eventFullDto;
    }


    @Override
    public EventFullDto updateEventsByAdmin(Long eventId, UpdateEventRequestDto requestDto) {
        Event event = getEvents(eventId);

        if (requestDto.getEventDate() != null && event.getPublishedOn() != null && requestDto.getEventDate().isBefore(event.getPublishedOn().plusHours(1))) {
            throw new BadRequestException("Нельзя изменять за час.");
        }
        if (requestDto.getStateAction() != null) {
            switch (requestDto.getStateAction()) {
                case PUBLISH_EVENT:
                    if (event.getState() != State.PENDING) {
                        throw new ConflictException("Состояние события должно быть PENDING");
                    }
                    event.setState(State.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    if (event.getState() == State.PUBLISHED) {
                        throw new ConflictException("Невозможно отменить опубликованное мероприятие");
                    }
                    event.setState(State.CANCELED);
                    break;
                case SEND_TO_REVIEW:
                case CANCEL_REVIEW:
                    if (event.getState() == State.PUBLISHED) {
                        throw new ConflictException("Состояние события должно быть на ожидании или отмененным");
                    }
                    break;
            }
        }
        updateEvent(event, requestDto);
        Event toUpdate = eventRepository.save(event);
        EventFullDto eventFullDto = EventMapper.toEventFullDto(toUpdate);
        eventFullDto.setConfirmedRequests(requestRepository.countAllByEventIdAndStatus(event.getId(),
                Status.CONFIRMED));
//        statsClient.setViewsNumber(eventFullDto);
        return eventFullDto;
    }

    @Override
    public List<EventShortDto> getEvents(String text,
                                         List<Long> categories,
                                         Boolean paid,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd,
                                         Boolean onlyAvailable,
                                         CustomSort sort,
                                         Integer from,
                                         Integer size,
                                         HttpServletRequest request) {
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(100);
        }
        if (rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException(String.format("Дата начала %s позже даты завершения %s.", rangeStart, rangeEnd));
        }
        statsService.saveHit(request.getRequestURI(), request.getRemoteAddr());
        Pageable pageable;
        if (sort.equals(CustomSort.EVENT_DATE)) {
            pageable = PageRequest.of(from == 0 ? 0 : from / size, size, Sort.by("eventDate").descending());
        } else {
            pageable = PageRequest.of(from == 0 ? 0 : from / size, size);
        }
        List<Event> events = eventRepository.getEventsToPublic(
                text != null ? text.toLowerCase() : null,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                pageable);
        List<EventShortDto> resultEvents = events
                .stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
        if (onlyAvailable) {
            List<Event> onlyAvailableEventList = events.stream().filter(event ->
                            event.getParticipantLimit() == 0 || event.getParticipantLimit() < requestRepository.getCountConfirmedRequestByEvent(event.getId())
                    )
                    .collect(Collectors.toList());
            resultEvents = onlyAvailableEventList.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
        }
        if (sort.equals(CustomSort.VIEWS)) {
            resultEvents.sort((o1, o2) -> Math.toIntExact(o2.getViews() - o1.getViews()));
        }
        return resultEvents;



//        CriteriaPublic criteria = CriteriaPublic.builder()
//                .text(text)
//                .categories(categories)
//                .paid(paid)
//                .onlyAvailable(onlyAvailable)
//                .sort(sort)
//                .from(from)
//                .size(size)
//                .build();
//        String ip = request.getRemoteAddr();
//        String uri = request.getRequestURI();
//        List<Event> events = customBuiltEventRepository.findEventsPublic(criteria);
//
//        List<EventShortDto> result = events.stream().map(EventMapper::mapToShortDto).collect(Collectors.toList());
//
//        if (result.size() > 0) {
//            statsService.setViewsNumber(result);
//
//            for (EventShortDto event : result) {
//                event.setConfirmedRequests(requestRepository.countAllByEventIdAndStatus(event.getId(),
//                        Status.CONFIRMED));
//            }
//        }
//
//
//
//        if (result.size() > 0) {
//            for (EventShortDto event : result) {
//                statsService.saveHit("/events/" + event.getId(), ip);
//            }
//        } else {
//            return new ArrayList<EventShortDto>();
//        }
//        if (criteria.getSort() == SortEvents.VIEWS) {
//            return result.stream().sorted(Comparator.comparingInt(EventShortDto::getViews)).collect(Collectors.toList());
//        }
//
//        return result.stream().sorted(Comparator.comparing(EventShortDto::getEventDate)).collect(Collectors.toList());

    }

    @Override
    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findByIdAndAndState(eventId, State.PUBLISHED).orElseThrow(() -> new NotFoundException("Не найдено опубликованное событие"));
        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        statsService.saveHit("/events/" + eventId, request.getRemoteAddr());
//        statsService.setViewsNumber(eventFullDto);
        eventFullDto.setConfirmedRequests(requestRepository.countAllByEventIdAndStatus(event.getId(), Status.CONFIRMED));
        return eventFullDto;
    }

    @Override
    public List<EventFullDto> getAllEventsByUserId(Long userId, Integer from, Integer size) {
        return eventRepository.findByInitiatorId(userId, PageRequest.of(from > 0 ? from / size : 0, size))
                .stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }


    @Override
    public EventFullDto getEventsByUserId(Long userId, Long eventId) {
        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId).orElseThrow(() -> new NotFoundException("Событие не найдено у пользователя!"));
        return EventMapper.toEventFullDto(event);
    }


    @Override
    public List<ParticipationRequestDto> getRequestUserEvents(Long userId, Long eventId) {
        User user = getUser(userId);
        Event event = getEvents(eventId);
        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException("Пользователь не инициатор события!");
        }
        return requestRepository.findByEventId(eventId)
                .stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());

    }


    public List<EventFullDto> getEventsToAdmin(List<Long> users,
                                               List<State> states,
                                               List<Long> categories,
                                               LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd,
                                               Integer from,
                                               Integer size) {
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(100);
        }
        if (rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException(String.format("Дата начала %s позже даты завершения %s.", rangeStart, rangeEnd));
        }
        List<Event> events = eventRepository.getEventsToAdmin(
                users,
                states,
                categories,
                rangeStart,
                rangeEnd,
                PageRequest.of(from > 0 ? from / size : 0, size));
        return events
                .stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());

//        Criteria criteria = Criteria.builder()
//                .users(userIds)
//                .states(states)
//                .categories(categories)
//                .from(from)
//                .size(size)
//                .rangeStart(start)
//                .rangeEnd(end)
//                .build();
//        List<Event> events = customBuiltEventRepository.getEvents(criteria);
//        var result = events.stream().map(EventMapper::toEventFullDto)
//                .map(statsService::setViewsNumber).collect(Collectors.toList());
//
//        return result;
    }


//    @Override
//    public List<EventShortDto> getEventsListInLocation(Long locationId, Float lat, Float lon, Float radius, Integer from, Integer size) {
//        Pageable page = PageRequest.of(from / size, size, Sort.by("eventDate").descending());
//        List<Event> events;
//
//        if (locationId != null) {
//            Location location = locationRepository.findById(locationId).orElseThrow(
//                    () -> new ru.practicum.main_service.exception.NotFoundException("Локация не найдена!"));
//            events = eventRepository.findEventsWithLocationRadius(
//                    location.getLat(),
//                    location.getLon(),
//                    location.getRadius(),
//                    State.PUBLISHED,
//                    page);
//        } else {
//            if (lat == null || lon == null) {
//                throw new ru.practicum.main_service.exception.NotFoundException("Точки не указаны!");
//            } else {
//                events = eventRepository.findEventsWithLocationRadius(
//                        lat, lon, radius, State.PUBLISHED, page);
//            }
//        }
//        var result = events.stream().map(EventMapper::mapToShortDto).collect(Collectors.toList());
//        return result;
//
//    }

    private void updateEvent(Event event, UpdateEventRequestDto updateEventRequestDto) {
        if (updateEventRequestDto.getAnnotation() != null) {
            event.setAnnotation(updateEventRequestDto.getAnnotation());
        }
        if (updateEventRequestDto.getCategory() != null) {
            event.setCategory(getCategory(updateEventRequestDto.getCategory()));
        }
        if (updateEventRequestDto.getDescription() != null) {
            event.setDescription(updateEventRequestDto.getDescription());
        }
        if (updateEventRequestDto.getEventDate() != null) {
            event.setEventDate(updateEventRequestDto.getEventDate());
        }
        if (updateEventRequestDto.getLocation() != null) {
            event.setLocation(getLocation(updateEventRequestDto.getLocation()));
        }
        if (updateEventRequestDto.getPaid() != null) {
            event.setPaid(updateEventRequestDto.getPaid());
        }
        if (updateEventRequestDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventRequestDto.getParticipantLimit());
        }
        if (updateEventRequestDto.getRequestModeration() != null) {
            event.setRequestModeration(updateEventRequestDto.getRequestModeration());
        }
        if (updateEventRequestDto.getTitle() != null) {
            event.setTitle(updateEventRequestDto.getTitle());
        }
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не существует."));
    }

    private Event getEvents(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Ивент не существует."));
    }

    public Category getCategory(Long catId) {
        return categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Не найдена выбранная категория"));
    }

    private Location getLocation(LocationDto locationDto) {
        Optional<Location> locationOptional = locationRepository.findByLatAndLon(locationDto.getLat(), locationDto.getLon());
        return locationOptional.orElseGet(() -> locationRepository.save(LocationMapper.toLocation(locationDto)));
    }
}
