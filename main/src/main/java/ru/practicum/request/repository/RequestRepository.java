package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.request.entity.Request;
import ru.practicum.request.enums.Status;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    @Query("select count(*) from Request as r where r.status = 'CONFIRMED' and r.event.id = :eventId")
    Long getCountConfirmedRequestByEvent(Long eventId);


    List<Request> findByRequesterId(Long id);

    List<Request> findByEventId(Long eventId);

    Long countAllByEventIdAndStatus(Long eventId, Status state);

    List<Request> findAllByIdIn(List<Long> ids);

    boolean existsRequestByRequester_IdAndEvent_Id(Long requesterId, Long eventId);

}
