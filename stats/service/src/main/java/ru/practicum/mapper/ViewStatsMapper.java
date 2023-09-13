package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ViewStatsDto;
import ru.practicum.entity.ViewStats;

@UtilityClass
public class ViewStatsMapper {
    public ViewStatsDto toViewStatsDto(ViewStats viewStats) {
        return ViewStatsDto.builder()
                .app(viewStats.getApp())
                .uri(viewStats.getUri())
                .hits(viewStats.getHits())
                .build();
    }
}