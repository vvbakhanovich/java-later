package ru.practicum.javalater.user.service;

import ru.practicum.javalater.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers();
    UserDto saveUser(UserDto user);
}
