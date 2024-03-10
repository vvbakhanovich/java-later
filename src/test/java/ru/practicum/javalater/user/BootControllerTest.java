package ru.practicum.javalater.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.javalater.user.dto.UserDto;
import ru.practicum.javalater.user.dto.UserState;
import ru.practicum.javalater.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
public class BootControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @Autowired
    MockMvc mvc;

    UserDto userDto1 = UserDto.builder()
            .id(1L)
            .firstName("firstname")
            .lastName("lastname")
            .email("test@test.com")
            .state(UserState.ACTIVE)
            .build();

    UserDto userDto2 = UserDto.builder()
            .id(2L)
            .firstName("firstname 2")
            .lastName("lastname 2")
            .email("tes2t@test.com")
            .state(UserState.ACTIVE)
            .build();

    @Test
    public void saveNewUser_shouldReturnResponseStatus200() throws Exception {
        when(userService.saveUser(userDto1))
                .thenReturn(userDto1);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$.firstName", is(userDto1.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(userDto1.getLastName())))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail())))
                .andExpect(jsonPath("$.state", is(userDto1.getState().name())));

        verify(userService, times(1)).saveUser(userDto1);
    }

    @Test
    public void saveNewUser_shouldReturnResponseStatus500() throws Exception {
        when(userService.saveUser(any())).thenThrow(new IllegalArgumentException());

        mvc.perform(post("/users")
                .content(mapper.writeValueAsString(userDto1))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(500))
                .andDo(print());

        verify(userService, times(1)).saveUser(any());
    }

    @Test
    @SneakyThrows
    public void getAllUsers_shouldReturnEmptyList() {
        when(userService.getAllUsers())
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/users")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(0)));

        verify(userService, times(1)).getAllUsers();
    }


    @Test
    @SneakyThrows
    public void getAllUsers_shouldReturnListOfTwoUsers() {
        when(userService.getAllUsers())
                .thenReturn(List.of(userDto1, userDto2));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$.[0].firstName", is(userDto1.getFirstName())))
                .andExpect(jsonPath("$.[0].email", is(userDto1.getEmail())))
                .andExpect(jsonPath("$.[1].firstName", is(userDto2.getFirstName())))
                .andExpect(jsonPath("$.[1].email", is(userDto2.getEmail())));

        verify(userService, times(1)).getAllUsers();
    }
}
