package ru.practicum.javalater.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.javalater.exception.InvalidArgumentExceptionHandler;
import ru.practicum.javalater.user.dto.UserDto;
import ru.practicum.javalater.user.dto.UserState;
import ru.practicum.javalater.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mvc;

    private ObjectMapper mapper;

    private UserDto userDto1;
    private UserDto userDto2;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(InvalidArgumentExceptionHandler.class)
                .build();
        mapper = new ObjectMapper();

        userDto1 = UserDto.builder()
                .id(1L)
                .firstName("firstname ")
                .lastName("lastame")
                .email("test@test.com")
                .state(UserState.ACTIVE)
                .build();

        userDto2 = UserDto.builder()
                .id(2L)
                .firstName("firstname ")
                .lastName("lastame")
                .email("test@test.com")
                .state(UserState.ACTIVE)
                .build();
    }

    @Test
    void saveNewUser() throws Exception {
        when(userService.saveUser(any()))
                .thenReturn(userDto1);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$.firstName", is(userDto1.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(userDto1.getLastName())))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail())));
    }

    @Test
    public void testGetAllUsers() throws Exception {
        when(userService.getAllUsers())
                        .thenReturn(List.of(userDto1, userDto2));

        mvc.perform(get("/users")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$.[0].id", is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$.[1].id", is(userDto2.getId()), Long.class))
                .andDo(print());
    }

    @Test
    void saveNewUserWithException() throws Exception {
        when(userService.saveUser(any()))
                .thenThrow(IllegalArgumentException.class);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(500));
    }
}