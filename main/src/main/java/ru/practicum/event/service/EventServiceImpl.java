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
import ru.practicum.event.dto.EventMapper;
import ru.practicum.event.dto.LocationMapper;
import ru.practicum.event.entity.Event;
import ru.practicum.event.entity.Location;
import ru.practicum.event.enums.CustomSort;
import ru.practicum.event.enums.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.LocationRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

import ru.practicum.exception.BadRequestException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.RequestMapper;
import ru.practicum.request.entity.Request;
import ru.practicum.request.enums.Status;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.users.entity.User;
import ru.practicum.users.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final StatsService statsService;
    private final CategoryRepository categoriesRepository;
    private final RequestRepository requestRepository;
    private final LocationRepository locationRepository;


    @Override
    public List<EventShortDto> getEvents(String text,
                                         List<Long> categories,
                                         Boolean paid,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd,
                                         Boolean onlyAvailable,
                                         CustomSort sort,
                                         Integer from,
                                         Integer size) {
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(30);
        }
        if (rangeEnd.isBefore(rangeStart)) {
            throw new BadRequestException("Начало позже конца");
        }
        Pageable pageable;
        if (sort.equals(CustomSort.EVENT_DATE)) {
            pageable = PageRequest.of(from == 0 ? 0 : from / size, size, Sort.by("eventDate").descending());
        } else {
            pageable = PageRequest.of(from == 0 ? 0 : from / size, size);
        }
//        statsService.hitEvent(null);
        List<Event> events = eventRepository.getAllEventForPub(
                text != null ? text.toLowerCase() : null,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                pageable);
        List<EventShortDto> eventShortDtoList = events
                .stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
        if (onlyAvailable) {
            List<Event> onlyAvailableEventList = events
                    .stream()
                    .filter(event -> event.getParticipantLimit() == 0
                            || event.getParticipantLimit() < requestRepository.getCountConfirmedRequestByEvent(event.getId()))
                    .collect(Collectors.toList());
            eventShortDtoList = onlyAvailableEventList.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
        }
        if (sort.equals(CustomSort.VIEWS)) {
            eventShortDtoList.sort((o1, o2) -> Math.toIntExact(o2.getViews() - o1.getViews()));
        }
        return eventShortDtoList;
    }

    @Override
    public EventFullDto getEventById(Long eventId) {
//        statsService.postHit(eventId);
        EventFullDto eventFullDto = EventMapper
                .toEventFullDto(eventRepository
                        .getEventByIdForPub(eventId)
                        .orElseThrow(() -> new NotFoundException("Событие не найдено.")));
        eventFullDto.setViews(1L);
        return eventFullDto;
    }

    @Override
    public List<EventFullDto> getEventsByUserId(Long userId, Integer from, Integer size) {
        return eventRepository
                .findByInitiatorId(userId, PageRequest.of(from > 0 ? from / size : 0, size))
                .stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto createEvents(Long userId, NewEventDto newEventDto) {
        if (newEventDto.getRequestModeration() == null) {
            newEventDto.setRequestModeration(true);
        }
        LocalDateTime eventDate = newEventDto.getEventDate();
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Событие не может быть раньше, чем через два часа от текущего момента!");
        }
        User user = getUser(userId);
        Location location = getLocation(newEventDto.getLocation());
        locationRepository.save(location);
        Category category = getCategory(newEventDto.getCategory());
        Event event = EventMapper.toEvent(newEventDto, category, location, user);
        event.setConfirmedRequests(0L);
        event.setViews(0L);
        event.setCreatedOn(LocalDateTime.now());
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getEventByUserId(Long userId, Long eventId) {
        return EventMapper
                .toEventFullDto(eventRepository
                        .findByInitiatorIdAndId(userId, eventId)
                        .orElseThrow(() -> new NotFoundException("событие не найдено у пользователя!")));
    }

    @Override
    public EventFullDto updateEventsByUser(Long userId, Long eventId, UpdateEventRequestDto updateEventRequestDto) {
        Event event = getEvent(eventId);
        if (event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Изменить можно только отмененные события или события в состоянии ожидания модерации!");
        }
        updateEvents(event, updateEventRequestDto);
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
        return eventFullDto;
    }

    @Override
    public List<ParticipationRequestDto> getRequestUserEvents(Long userId, Long eventId) {
        User user = getUser(userId);
        Event event = getEvent(eventId);

        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException("Пользователь не инициатор события!");
        }

        List<Request> requests = requestRepository.findByEventId(eventId);
        return requests.stream().map(RequestMapper::toRequestDto).collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult updateStatusRequestByUserIdForEvents(Long userId, Long eventId,
                                                                               EventRequestStatusUpdateRequest requestStatusUpdate) {
        User user = getUser(userId);
        Event event = getEvent(eventId);

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
        List<Request> requestsToUpdate = requestRepository.findAllByIdIn(requestStatusUpdate.getRequestIds());
        List<Request> confirmed = new ArrayList<>();
        List<Request> rejected = new ArrayList<>();

        for (Request request : requestsToUpdate) {

            if (!request.getStatus().equals(Status.PENDING)) {
                continue;
            }


            if (!request.getEvent().getId().equals(eventId)) {
                rejected.add(request);
                continue;
            }
            if (requestStatusUpdate.getStatus().equals("CONFIRMED")) {
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
        eventRepository.save(event);
        requestRepository.saveAll(requestsToUpdate);

        return RequestMapper.toUpdateResultDto(confirmed, rejected);
    }

    @Override
    public List<EventFullDto> getEventsByAdmin(List<Long> userIds, List<State> states, List<Long> categories, String rangeStart, String rangeEnd, Integer from, Integer size) {
        LocalDateTime start = null;
        LocalDateTime end = null;
        if (rangeStart != null) {
            start = LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (rangeEnd != null) {
            end = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (start != null && end != null) {
            if (start.isAfter(end)) {
                throw new BadRequestException("Дата начала  не может быть после даты завершения.");
            }
        }
        return eventRepository.getAllEventForAdmin(userIds,
                states,
                categories,
                start,
                end,
                PageRequest.of(from == 0 ? 0 : from / size, size))
                .stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventRequestDto updateEventRequestDto) {
        Event event = getEvent(eventId);
        if (updateEventRequestDto.getEventDate() != null
                && event.getPublishedOn() != null
                && updateEventRequestDto.getEventDate().isBefore(event.getPublishedOn().plusHours(1))) {
            throw new BadRequestException("Дата начала изменяемого события должна быть не ранее чем за час от даты публикации");
        }
        if (updateEventRequestDto.getStateAction() != null) {
            switch (updateEventRequestDto.getStateAction()) {
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
        updateEvents(event, updateEventRequestDto);
        Event toUpdate = eventRepository.save(event);
        EventFullDto eventFullDto = EventMapper.toEventFullDto(toUpdate);
        eventFullDto.setConfirmedRequests(requestRepository.countAllByEventIdAndStatus(event.getId(),
                Status.CONFIRMED));
        return eventFullDto;
    }

    private void updateEvents(Event event, UpdateEventRequestDto requestDto) {
        if (requestDto.getAnnotation() != null) {
            event.setAnnotation(requestDto.getAnnotation());
        }
        if (requestDto.getCategory() != null) {
            Category categories = getCategory(requestDto.getCategory());
            event.setCategory(categories);
        }
        if (requestDto.getDescription() != null) {
            event.setDescription(requestDto.getDescription());
        }
        if (requestDto.getEventDate() != null) {
            event.setEventDate(requestDto.getEventDate());
        }
        if (requestDto.getLocation() != null) {
            Location location = getLocation(requestDto.getLocation());
            event.setLocation(location);
        }
        if (requestDto.getPaid() != null) {
            event.setPaid(requestDto.getPaid());
        }
        if (requestDto.getParticipantLimit() != null) {
            event.setParticipantLimit(requestDto.getParticipantLimit());
        }
        if (requestDto.getRequestModeration() != null) {
            event.setRequestModeration(requestDto.getRequestModeration());
        }
        if (requestDto.getTitle() != null) {
            event.setTitle(requestDto.getTitle());
        }
    }

    private User getUser(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя не существует"));
    }

    private Event getEvent(Long eventId) {
        return eventRepository
                .findById(eventId)
                .orElseThrow(() -> new NotFoundException("События не существует"));
    }

    public Category getCategory(Long catId) {
        return categoriesRepository
                .findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория не существует"));
    }

    private Location getLocation(LocationDto locationDto) {
        return locationRepository
                .findByLatAndLon(locationDto.getLat(), locationDto.getLon())
                .orElseGet(() -> locationRepository.save(LocationMapper.toLocation(locationDto)));
    }
}
