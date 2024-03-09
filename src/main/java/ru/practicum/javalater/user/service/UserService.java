package ru.practicum.javalater.user.service;

import ru.practicum.javalater.user.dto.UserDto;
import ru.practicum.javalater.user.entity.User;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers();
    UserDto saveUser(UserDto user);
}
