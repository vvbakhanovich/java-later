package ru.practicum.javalater.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.javalater.item.dto.GetItemRequest;
import ru.practicum.javalater.item.dto.ItemDto;
import ru.practicum.javalater.item.dto.ModifyItemRequest;
import ru.practicum.javalater.item.dto.PostItemDto;
import ru.practicum.javalater.item.service.ItemService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto add(@RequestHeader("X-Later-User-Id") Long userId, @RequestBody PostItemDto itemDto) {
        return itemService.addNewItem(userId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Later-User-Id") long userId, @PathVariable long itemId) {
        itemService.deleteItem(userId, itemId);
    }

    @GetMapping("/find")
    public List<ItemDto> findByUserIdAndTags(@RequestHeader("X-Later-User-Id") long userId, @RequestParam Set<String> tags) {
        return itemService.findByUserIdAndTags(userId, tags);
    }

    @GetMapping
    public List<ItemDto> get(@RequestHeader("X-Later-User-Id") long userId,
                             @RequestParam(defaultValue = "unread") String state,
                             @RequestParam(defaultValue = "all") String contentType,
                             @RequestParam(defaultValue = "newest") String sort,
                             @RequestParam(defaultValue = "10") int limit,
                             @RequestParam(required = false) List<String> tags) {

        return itemService.searchItems(GetItemRequest.of(userId, state, contentType, sort, limit, tags));
    }

    @PatchMapping
    public ItemDto modifyItem(@RequestHeader("X-Later-User-Id") long userId,
                              @RequestBody ModifyItemRequest request) {
        return itemService.modifyItem(userId, request);
    }
}
