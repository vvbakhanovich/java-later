package ru.practicum.javalater.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.javalater.user.dto.UserDto;
import ru.practicum.javalater.user.dto.UserState;
import ru.practicum.javalater.user.entity.User;
import ru.practicum.javalater.user.mapper.UserMapper;
import ru.practicum.javalater.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void saveUser_shouldReturnSavedUser() {
        UserDto userDto = createDto(1);
        User user = createUser(1);
        when(userMapper.toModel(userDto))
                        .thenReturn(user);
        when(userRepository.save(user))
                .thenReturn(user);
        when(userMapper.toDto(user))
                .thenReturn(userDto);

        UserDto savedUser = userService.saveUser(userDto);

        assertThat(savedUser, is(userDto));
        verify(userMapper, times(1)).toModel(userDto);
        verify(userMapper, times(1)).toDto(user);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void getAllUsers_shouldReturnEmptyList() {
        when(userRepository.findAll())
                .thenReturn(Collections.emptyList());
        when(userMapper.toDtoList(Collections.emptyList()))
                .thenReturn(Collections.emptyList());

        List<UserDto> allUsers = userService.getAllUsers();

        assertThat(allUsers, is(Collections.emptyList()));
        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).toDtoList(Collections.emptyList());
    }

    @Test
    public void getAllUsers_shouldReturnListOfUser() {
        User user = createUser(1);
        UserDto userDto = createDto(1);
        when(userRepository.findAll())
                .thenReturn(List.of(user));
        when(userMapper.toDtoList(List.of(user)))
                .thenReturn(List.of(userDto));

        List<UserDto> allUsers = userService.getAllUsers();

        assertThat(allUsers, is(List.of(userDto)));
        assertThat(allUsers.size(), is(1));
        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).toDtoList(List.of(user));
    }

    private UserDto createDto(int id) {
        return UserDto.builder()
                .firstName("firstname " + id)
                .lastName("lastame")
                .email("test" + id + "@test.com")
                .state(UserState.ACTIVE)
                .build();
    }

    private User createUser(int id) {
        return User.builder()
                .firstName("firstname " + id)
                .lastName("lastame")
                .email("test" + id + "@test.com")
                .state(UserState.ACTIVE)
                .build();
    }

}