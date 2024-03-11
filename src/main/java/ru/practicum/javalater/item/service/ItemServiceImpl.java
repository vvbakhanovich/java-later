package ru.practicum.javalater.item.service;

import com.google.common.collect.Lists;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.javalater.item.dto.GetItemRequest;
import ru.practicum.javalater.item.dto.ItemDto;
import ru.practicum.javalater.item.dto.ModifyItemRequest;
import ru.practicum.javalater.item.dto.PostItemDto;
import ru.practicum.javalater.item.entity.Item;
import ru.practicum.javalater.item.entity.QItem;
import ru.practicum.javalater.item.exception.NotAuthorizedException;
import ru.practicum.javalater.item.mapper.ItemMapper;
import ru.practicum.javalater.item.metadata.UrlMetadataRetriever;
import ru.practicum.javalater.item.repository.ItemRepository;
import ru.practicum.javalater.user.entity.User;
import ru.practicum.javalater.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private final UrlMetadataRetriever urlMetadataRetriever;

    @Override
    public List<ItemDto> getItems(long userId) {
        log.info("Получение сохраненных ссылок пользователя с id '{}'", userId);
        return itemMapper.toDtoList(itemRepository.findByUserId(userId));
    }

    @Override
    @Transactional
    public ItemDto addNewItem(Long userId, PostItemDto itemDto) {
        User user = findUser(userId);
        Item item = itemMapper.toModel(itemDto);
        UrlMetadataRetriever.UrlMetadata metadata = urlMetadataRetriever.retrieve(item.getUrl());
        Optional<Item> optionalItem = itemRepository.findByResolvedUrl(metadata.getResolvedUrl());
        if (optionalItem.isPresent()) {
            Item storedItem = optionalItem.get();
            storedItem.getTags().addAll(item.getTags());
            return itemMapper.toDto(itemRepository.save(storedItem));
        }
        setMetadataToItem(item, metadata);
        item.setUser(user);
        log.info("Пользователь с id '{}' добавил ссылку: {}.", userId, item);
        return itemMapper.toDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public void deleteItem(long userId, long itemId) {
        findAndCheckPermission(userId, itemId);
        log.info("Пользователь с id '{}' удаляет ссылку с id '{}", userId, itemId);
        itemRepository.deleteByUserIdAndId(userId, itemId);
    }

    @Override
    public List<ItemDto> findByUserIdAndTags(long userId, Set<String> tags) {
        return itemMapper.toDtoList(itemRepository.findByUserAndTags(userId, tags));
    }

    @Override
    public List<ItemDto> searchItems(GetItemRequest req) {

        findUser(req.getUserId());
        List<BooleanExpression> conditions = new ArrayList<>();
        BooleanExpression byUserId = QItem.item.user.id.eq(req.getUserId());
        conditions.add(byUserId);

        if (!req.getState().equals(GetItemRequest.State.ALL)) {
            conditions.add(makeStateCondition(req.getState()));
        }

        if (!req.getContentType().equals(GetItemRequest.ContentType.ALL)) {
            conditions.add(makeContentTypeCondition(req.getContentType()));
        }

        if (req.hasTags()) {
            conditions.add(QItem.item.tags.any().in(req.getTags()));
        }

        BooleanExpression finalCondition = conditions.stream()
                .reduce(BooleanExpression::and)
                .get();

        Sort sort = makeOrderByClause(req.getSort());
        PageRequest pageRequest = PageRequest.of(0, req.getLimit(), sort);

        Iterable<Item> items = itemRepository.findAll(finalCondition, pageRequest);
        return itemMapper.toDtoList(Lists.newArrayList(items));
    }

    @Override
    public ItemDto modifyItem(long userId, ModifyItemRequest request) {
        Item item = findAndCheckPermission(userId, request.getItemId());
        item.setUnread(request.isUnread());
        if (request.isReplaceTags()) {
            item.getTags().clear();
        }
        if (request.hasTags()) {
            item.getTags().addAll(request.getTags());
        }
        Item saved = itemRepository.save(item);
        return itemMapper.toDto(saved);
    }

    private Item findAndCheckPermission(long userId, long itemId) {
        findUser(userId);
        Item item = findItem(itemId);
        if (!item.getUser().getId().equals(userId)) {
            throw new NotAuthorizedException("Неавторизованный доступ!");
        } return item;
    }

    private Item findItem(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Ссылка с id '" + itemId + "' не найдена."));
    }

    private BooleanExpression makeStateCondition(GetItemRequest.State state) {
        switch (state) {
            case UNREAD:
                return QItem.item.unread.isTrue();
            case READ:
                return QItem.item.unread.isFalse();
            default:
                throw new UnsupportedOperationException("Неизвестный статус");
        }
    }

    private BooleanExpression makeContentTypeCondition(GetItemRequest.ContentType contentType) {
        switch (contentType) {
            case ARTICLE:
                return QItem.item.mimeType.eq("text");
            case IMAGE:
                return QItem.item.mimeType.eq("image");
            case VIDEO:
                return QItem.item.mimeType.eq("video");
            default:
                throw new UnsupportedOperationException("Неизвестный тип контента");
        }
    }

    private Sort makeOrderByClause(GetItemRequest.Sort sort) {
        switch (sort) {
            case NEWEST:
                return Sort.by(Sort.Direction.ASC, "dateResolved");
            case OLDEST:
                return Sort.by(Sort.Direction.DESC, "dateResolved");
            case TITLE:
                return Sort.by(Sort.Direction.ASC, "title");
            default:
                throw new UnsupportedOperationException("Неизвестный тип сортировки");
        }
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("No user with id '" + userId + "'."));
    }


    private void setMetadataToItem(Item item, UrlMetadataRetriever.UrlMetadata metadata) {
        item.setResolvedUrl(metadata.getResolvedUrl());
        item.setHasImage(metadata.isHasImage());
        item.setHasVideo(metadata.isHasVideo());
        item.setTitle(metadata.getTitle());
        item.setMimeType(metadata.getMimeType());
        item.setDateResolved(metadata.getDateResolved());
    }
}
