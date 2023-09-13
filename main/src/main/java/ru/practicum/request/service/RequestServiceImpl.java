package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.event.entity.Event;
import ru.practicum.event.enums.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.RequestMapper;
import ru.practicum.request.entity.Request;
import ru.practicum.request.enums.Status;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.users.entity.User;
import ru.practicum.users.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestsRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User user = getUser(userId);
        Event event = getEvent(eventId);
        Long confirmedRequestAmount = requestsRepository.countAllByEventIdAndStatus(eventId, Status.CONFIRMED);
        if (user.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException("Нельзя отсутствовать на собственном событии");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Нельзя создавать запрос на неопубликованное событие");
        }
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= confirmedRequestAmount) {
            throw new ConflictException("Достигнут лимит на участие в событии");
        }
        if (requestsRepository.existsRequestByRequester_IdAndEvent_Id(userId, eventId)) {
            throw new ConflictException("Инициатор события не может добавить запрос на участие в своём событии");
        }
        Request request = Request
                .builder()
                .requester(user)
                .event(event)
                .created(LocalDateTime.now())
                .build();
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(Status.CONFIRMED);
        } else {
            request.setStatus(Status.PENDING);
        }
        return RequestMapper.toRequestDto(requestsRepository.save(request));
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        getUser(userId);
        Request request = getRequest(requestId);
        if (!userId.equals(request.getRequester().getId())) {
            throw new ConflictException("Нельзя отменить чужой запрос");
        }
        request.setStatus(Status.CANCELED);
        return RequestMapper.toRequestDto(requestsRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId) {
        getUser(userId);
        return requestsRepository.findByRequesterId(userId)
                .stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    public User getUser(Long userid) {
        return userRepository
                .findById(userid)
                .orElseThrow(() -> new NotFoundException("Пользователь не существует"));

    }

    public Event getEvent(Long eventId) {
        return eventRepository
                .findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не существует"));
    }

    public Request getRequest(Long requestId) {
        return requestsRepository
                .findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не существует"));
    }
}
