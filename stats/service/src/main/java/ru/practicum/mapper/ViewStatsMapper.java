package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ViewStatsDto;
import ru.practicum.entity.ViewStats;

@UtilityClass
public final class ViewStatsMapper {
    public static ViewStats toViewStats(ViewStatsDto viewStatsDto) {
        return ViewStats.builder()
                .app(viewStatsDto.getApp())
                .uri(viewStatsDto.getUri())
                .hits(viewStatsDto.getHits())
                .build();
    }

    public static ViewStatsDto toViewStatsDto(ViewStats viewStats) {
        return ViewStatsDto.builder()
                .app(viewStats.getApp())
                .uri(viewStats.getUri())
                .hits(viewStats.getHits())
                .build();
    }
}