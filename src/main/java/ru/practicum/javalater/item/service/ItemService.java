package ru.practicum.javalater.item.service;

import ru.practicum.javalater.item.dto.GetItemRequest;
import ru.practicum.javalater.item.dto.ItemDto;
import ru.practicum.javalater.item.dto.ModifyItemRequest;
import ru.practicum.javalater.item.dto.PostItemDto;

import java.util.List;
import java.util.Set;

public interface ItemService {
    List<ItemDto> getItems(long userId);

    ItemDto addNewItem(Long userId, PostItemDto itemDto);

    void deleteItem(long userId, long itemId);

    List<ItemDto> findByUserIdAndTags(long userId, Set<String> tags);

    List<ItemDto> searchItems(GetItemRequest req);

    ItemDto modifyItem(long userId, ModifyItemRequest request);
}
