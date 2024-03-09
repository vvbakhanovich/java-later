package ru.practicum.javalater.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;

    private String firstName;

    private String lastName;

    private String registrationDate;

    private String email;

    private UserState state;

    @JsonFormat(pattern = "yyyy.MM.dd")
    private LocalDate dateOfBirth;
}
