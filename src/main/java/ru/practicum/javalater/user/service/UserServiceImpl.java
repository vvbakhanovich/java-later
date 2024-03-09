package ru.practicum.javalater.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.javalater.user.dto.UserDto;
import ru.practicum.javalater.user.entity.User;
import ru.practicum.javalater.user.mapper.UserMapper;
import ru.practicum.javalater.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getAllUsers() {
        return userMapper.toDtoList(userRepository.findAll());
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        User user = userMapper.toModel(userDto);
        return userMapper.toDto(userRepository.save(user));
    }
}
