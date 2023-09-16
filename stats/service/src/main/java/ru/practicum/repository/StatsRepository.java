package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.entity.EndpointHit;
import ru.practicum.entity.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {
    @Query("select new ru.practicum.entity.ViewStats(e.app, e.uri, count(e.ip)) " +
            "from EndpointHit as e " +
            "where e.timestamp between :start and :end " +
            "and e.uri in :uris or :uris is null " +
            "group by e.app, e.uri " +
            "order by 3 desc")
    List<ViewStats> findViewStats(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.entity.ViewStats(e.app, e.uri, count(distinct e.ip)) " +
            "from EndpointHit as e " +
            "where e.timestamp between :start and :end " +
            "and e.uri in :uris or :uris is null " +
            "group by e.app, e.uri " +
            "order by 3 desc")
    List<ViewStats> findUniqueViewStats(LocalDateTime start, LocalDateTime end, List<String> uris);
}
