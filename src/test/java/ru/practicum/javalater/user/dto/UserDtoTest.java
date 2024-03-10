package ru.practicum.javalater.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoTest {

    @Autowired
    JacksonTester<UserDto> json;

    @Test
    void checkDateToStringConversion() throws IOException {
        UserDto userDto1 = UserDto.builder()
                .id(1L)
                .firstName("firstname ")
                .lastName("lastname")
                .email("test@test.com")
                .state(UserState.ACTIVE)
                .dateOfBirth(LocalDate.of(2024, 3, 9))
                .build();

        JsonContent<UserDto> result = json.write(userDto1);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.dateOfBirth").isEqualTo("2024.03.09");
    }
}