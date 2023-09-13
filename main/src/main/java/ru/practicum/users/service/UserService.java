package ru.practicum.users.service;

import ru.practicum.users.dto.NewUserRequestDto;
import ru.practicum.users.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(NewUserRequestDto userRequestDto);

    void deleteUser(Long userId);

    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);
}
