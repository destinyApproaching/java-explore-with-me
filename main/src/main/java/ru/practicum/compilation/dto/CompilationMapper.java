package ru.practicum.compilation.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.compilation.entity.Compilation;
import ru.practicum.event.dto.EventMapper;
import ru.practicum.event.entity.Event;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CompilationMapper {
    public Compilation toCompilation(NewCompilationDto newCompilationDto, List<Event> events) {
        return Compilation.builder()
                .pinned(newCompilationDto.getPinned())
                .title(newCompilationDto.getTitle())
                .events(events)
                .build();

    }

    public CompilationDto toCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .events(compilation
                        .getEvents()
                        .stream()
                        .map(EventMapper::toEventShortDto)
                        .collect(Collectors.toList()))
                .build();
    }
}
