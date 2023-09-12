package ru.practicum.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.event.entity.Event;
import ru.practicum.event.enums.State;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("select e from  Event as e " +
            "where (e.state = 'PUBLISHED') " +
            "and (e.eventDate between :rangeStart and :rangeEnd) " +
            "and (e.paid = :paid or :paid is null) and (e.category.id in :categories or :categories is null) " +
            "and (:text is null or lower(e.annotation) like lower(concat('%', :text, '%')) or lower(e.description) like lower(concat('%', :text, '%')))")
    List<Event> getEventsToPublic(String text,
                                  List<Long> categories,
                                  Boolean paid,
                                  LocalDateTime rangeStart,
                                  LocalDateTime rangeEnd,
                                  Pageable pageable);

    @Query("select e from Event as e " +
            "where e.eventDate between :rangeStart and :rangeEnd " +
            "and e.initiator.id in :users or :users is null " +
            "and e.state in :states or :states is null " +
            "and e.category.id in :categories or :categories is null ")
    List<Event> getEventsToAdmin(List<Long> users,
                                    List<State> states,
                                    List<Long> categories,
                                    LocalDateTime rangeStart,
                                    LocalDateTime rangeEnd,
                                    Pageable pageable);

    Optional<Event> findByIdAndAndState(Long eventId, State state);

    List<Event> findByInitiatorId(Long initiatorId, Pageable pageable);

    Optional<Event> findByInitiatorIdAndId(Long initiatorId, Long eventId);

    List<Event> findAllByIdIn(List<Long> ids);

    boolean existsEventsByCategory_Id(Long catId);

//    @Query("SELECT e FROM Event e " +
//            "WHERE FUNCTION('distance', :lat, :lon, e.location.lat, e.location.lon) " +
//            "<= :radius " +
//            "AND e.state = :state " +
//            "ORDER BY e.eventDate DESC ")
//    List<Event> findEventsWithLocationRadius(
//            Float lat,
//            Float lon,
//            Float radius,
//            State state,
//            Pageable pageable);
}
