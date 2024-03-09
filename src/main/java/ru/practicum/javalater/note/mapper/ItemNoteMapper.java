package ru.practicum.javalater.note.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.javalater.note.dto.ItemNoteDto;
import ru.practicum.javalater.note.entity.ItemNote;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemNoteMapper {

    @Mapping(source = "item.id", target = "itemId")
    @Mapping(source = "item.url", target = "itemUrl")
    ItemNoteDto toDto(ItemNote itemNote);

    List<ItemNoteDto> toDtoList(List<ItemNote> itemNotes);

    ItemNote toModel(ItemNoteDto itemNoteDto);

    default String instantToString(Instant instant) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd, HH:mm:ss");
        return instant == null ? null : LocalDateTime.ofInstant(instant, ZoneOffset.ofHours(3)).format(formatter);
    }

    default Instant stringToInstant(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd, HH:mm:ss");
        return dateTime == null ? null : LocalDateTime.parse(dateTime, formatter).toInstant(ZoneOffset.ofHours(3));
    }
}
