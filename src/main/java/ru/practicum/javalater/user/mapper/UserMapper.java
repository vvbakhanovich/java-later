package ru.practicum.javalater.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.javalater.user.dto.UserDto;
import ru.practicum.javalater.user.entity.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);

    User toModel(UserDto userDto);

    List<UserDto> toDtoList(List<User> users);

    default String instantToString(Instant instant) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd, HH:mm:ss");
        return instant == null ? null : LocalDateTime.ofInstant(instant, ZoneOffset.ofHours(3)).format(formatter);
    }

    default Instant stringToInstant(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd, HH:mm:ss");
        return dateTime == null ? null : LocalDateTime.parse(dateTime, formatter).toInstant(ZoneOffset.ofHours(3));
    }
}
