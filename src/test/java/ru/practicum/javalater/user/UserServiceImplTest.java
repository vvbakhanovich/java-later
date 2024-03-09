package ru.practicum.javalater.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.javalater.user.dto.UserDto;
import ru.practicum.javalater.user.dto.UserState;
import ru.practicum.javalater.user.entity.User;
import ru.practicum.javalater.user.service.UserServiceImpl;

import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
class UserServiceImplTest {

    private final UserServiceImpl userService;


    @Test
    public void testFindTwoUsers() {
        UserDto user1 = createDto(1);
        UserDto user2 = createDto(2);
        UserDto user3 = userService.saveUser(user1);
        UserDto user4 = userService.saveUser(user2);
        List<UserDto> allUsers = userService.getAllUsers();
        assertThat(allUsers, hasItems(user3, user4));
        assertThat(allUsers, is(List.of(user3, user4)));
        assertThat(allUsers, hasSize(2));
    }

    @Test
    public void findNoUsers() {
        List<UserDto> allUsers = userService.getAllUsers();
        assertThat(allUsers, notNullValue());
        assertThat(allUsers, empty());
    }

    private UserDto createDto(int id) {
        return UserDto.builder()
                .firstName("firstname " + id)
                .lastName("lastame")
                .email("test" + id + "@test.com")
                .state(UserState.ACTIVE)
                .build();
    }
}