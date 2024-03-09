package ru.practicum.javalater.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.javalater.item.dto.ItemDto;
import ru.practicum.javalater.item.dto.PostItemDto;
import ru.practicum.javalater.item.entity.Item;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(source = "user.id", target = "userId")
    ItemDto toDto(Item item);

    Item toModel(ItemDto itemDto);

    Item toModel(PostItemDto itemDto);

    List<ItemDto> toDtoList(List<Item> items);
}
