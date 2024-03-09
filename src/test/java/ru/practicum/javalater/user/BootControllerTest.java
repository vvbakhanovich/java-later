package ru.practicum.javalater.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.javalater.user.dto.UserDto;
import ru.practicum.javalater.user.dto.UserState;
import ru.practicum.javalater.user.service.UserService;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
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
            .firstName("firstname ")
            .lastName("lastame")
            .email("test@test.com")
            .state(UserState.ACTIVE)
            .build();

    @Test
    public void test() throws Exception {
        Mockito.when(userService.saveUser(any()))
                .thenReturn(userDto1);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$.firstName", Matchers.is(userDto1.getFirstName())))
                .andExpect(jsonPath("$.lastName", Matchers.is(userDto1.getLastName())))
                .andExpect(jsonPath("$.email", Matchers.is(userDto1.getEmail())))
                .andExpect(jsonPath("$.state", Matchers.is(userDto1.getState().name())));

    }

    @Test
    public void test2() throws Exception {
        Mockito.when(userService.saveUser(any())).thenThrow(new IllegalArgumentException());

        mvc.perform(post("/users")
                .content(mapper.writeValueAsString(userDto1))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(500))
                .andDo(print());
    }
}
