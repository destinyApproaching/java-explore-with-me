package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationMapper;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.entity.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.entity.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events = new ArrayList<>();
        if (newCompilationDto.getEvents() != null) {
            events = eventRepository.findAllById(newCompilationDto.getEvents());
        }
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);
        compilation.setEvents(events);
        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = getCompilation(compId);
        if (updateCompilationRequest.getEvents() != null) {
            compilation.setEvents(eventRepository.findAllByIdIn(updateCompilationRequest.getEvents()));
        }
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }

//        Compilation result = compilationRepository.save(compilation);
//
//        List<Event> events = new ArrayList<>();
//        if (updateCompilationRequest.getEvents() != null) {
//            events = eventRepository.findAllById(updateCompilationRequest.getEvents());
//        }
//        return CompilationMapper.toCompilationDto(result, maptoDto(events));

        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    public void deleteCompilation(Long compId) {
        Compilation compilation = getCompilation(compId);
        compilationRepository.delete(compilation);
    }

    @Override
    public List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size) {
//        int offset = from > 0 ? from / size : 0;
//        PageRequest page = PageRequest.of(offset, size);
//        List<Compilation> compilations;
//        //искать только закрепленные/не закрепленные подборки
//        //В случае, если по заданным фильтрам не найдено ни одной подборки, возвращает пустой список
//        if (pinned == null) {
//            compilations = compilationRepository.findAll(page).getContent();
//        } else {
//            compilations = compilationRepository.findAllByPinned(pinned, page);
//        }
//        if (compilations.isEmpty()) {
//            return Collections.emptyList();
//        }
//        List<CompilationDto> collect = compilations.stream().map(compilation ->
//                CompilationMapper.toCompilationDto(compilation,
//                        maptoDto(compilation.getEvents()))).collect(Collectors.toList());
//        return collect;

        return compilationRepository.findAllByPinnedOrderById(pinned, PageRequest.of(from > 0 ? from / size : 0, size))
                .stream()
                .map(CompilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = getCompilation(compId);
        return CompilationMapper.toCompilationDto(compilation);
    }

//    private List<Event> getFromId(List<Long> evenIdList) {
//        List<Event> events = eventRepository.findAllByIdIn(evenIdList);
//        //Если размер списка  из репозитория != evenIdList, то -> не все события с id  были найдены
//        if (events.size() != evenIdList.size()) {
//            List<Long> list = new ArrayList<>();
//            for (Event event : events) {
//                Long id = event.getId();
//                list.add(id);
//            }
//            //удаляем из списка evenIdList id событий, которые были в базе данных
//            //-> evenIdList останется только id событий, которые не были найдены
//            evenIdList.removeAll(list);
//        }
//        return events;
//    }

//    private List<EventShortDto> maptoDto(List<Event> events) {
//        List<EventShortDto> eventShortDto = events.stream().map(event ->
//                EventMapper.toEventShortDto(
//                        event,
//                        CategoryMapper.toCategoryDto(event.getCategory()),
//                        UserMapper.toUserDto(event.getInitiator())
//                )).collect(Collectors.toList());
//        return eventShortDto;
//    }

    private Compilation getCompilation(Long compId) {
        return compilationRepository.findById(compId).orElseThrow(() -> new NotFoundException("Подборка не найдена по id."));
    }

}
