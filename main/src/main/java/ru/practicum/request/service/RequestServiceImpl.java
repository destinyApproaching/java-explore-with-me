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
import ru.practicum.user.entity.User;
import ru.practicum.user.repository.UserRepository;

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
    public List<ParticipationRequestDto> getRequest(Long userId) {
        getUserById(userId);
        List<Request> requestsList = requestsRepository.findByRequesterId(userId);

        return requestsList.stream().map(RequestMapper::toRequestDto).collect(Collectors.toList());
    }

    /*
    нельзя добавить повторный запрос (Ожидается код ошибки 409)
    инициатор события не может добавить запрос на участие в своём событии (Ожидается код ошибки 409)
    нельзя участвовать в неопубликованном событии (Ожидается код ошибки 409)
    если у события достигнут лимит запросов на участие - необходимо вернуть ошибку (Ожидается код ошибки 409)
    если для события отключена пре-модерация запросов на участие, то запрос должен автоматически перейти в состояние подтвержденного
     */
    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User user = getUserById(userId);
        Event event = getEventsById(eventId);
        Long confirmedRequestAmount = requestsRepository.countAllByEventIdAndStatus(eventId, Status.CONFIRMED);
        // выбирает из Request, где поле `event.id` равно заданному `eventId` и поле `status` равно "CONFIRMED"
        if (user.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException("инициатор события не может добавить запрос на участие в своём событии");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("нельзя участвовать в неопубликованном событии!");
        }
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= confirmedRequestAmount) {
            throw new ConflictException("достигнут лимит запросов на участие!");
        }
        if (requestsRepository.existsRequestByRequester_IdAndEvent_Id(userId, eventId)) {
            throw new ConflictException("инициатор события не может добавить запрос на участие в своём событии");
        }

        Request request = Request.builder()
                .requester(user)
                .event(event)
                .created(LocalDateTime.now())
                .build();
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) { // нужна ли модерация на участие
            request.setStatus(Status.CONFIRMED);
        } else {
            request.setStatus(Status.PENDING);
        }
        return RequestMapper.toRequestDto(requestsRepository.save(request));
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        getUserById(userId);
        Request request = getRequestById(requestId);
        if (!userId.equals(request.getRequester().getId())) {
            throw new ConflictException("Можно отменить только свой запрос на участие!");
        }
        request.setStatus(Status.CANCELED);
        return RequestMapper.toRequestDto(requestsRepository.save(request));
    }

    public User getUserById(Long userid) {
        return userRepository.findById(userid).orElseThrow(
                () -> new NotFoundException("Не найден пользователь по id!"));

    }

    public Event getEventsById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Не найдено мероприятие по id!"));
    }

    public Request getRequestById(Long requestId) {
        return requestsRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("Не найден запрос по id!"));
    }
}
