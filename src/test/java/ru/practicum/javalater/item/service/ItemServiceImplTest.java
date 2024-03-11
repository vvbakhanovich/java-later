package ru.practicum.javalater.item.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.javalater.item.dto.GetItemRequest;
import ru.practicum.javalater.item.dto.ItemDto;
import ru.practicum.javalater.item.dto.ModifyItemRequest;
import ru.practicum.javalater.item.dto.PostItemDto;
import ru.practicum.javalater.item.entity.Item;
import ru.practicum.javalater.item.exception.NotAuthorizedException;
import ru.practicum.javalater.item.mapper.ItemMapper;
import ru.practicum.javalater.item.metadata.UrlMetadataRetriever;
import ru.practicum.javalater.item.metadata.UrlMetadataRetrieverImpl;
import ru.practicum.javalater.item.repository.ItemRepository;
import ru.practicum.javalater.user.entity.User;
import ru.practicum.javalater.user.repository.UserRepository;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.javalater.item.entity.QItem.item;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private UrlMetadataRetriever urlMetadataRetriever;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Captor
    ArgumentCaptor<Item> itemArgumentCaptor;

    @Captor
    ArgumentCaptor<BooleanExpression> booleanExpressionArgumentCaptor;

    @Test
    void getItems_ReturnListOfItem() {
        ItemDto itemDto = new ItemDto();
        Item item = new Item();
        long userId = 1;
        when(itemRepository.findByUserId(userId))
                .thenReturn(List.of(item));
        when(itemMapper.toDtoList(List.of(item)))
                .thenReturn(List.of(itemDto));

        List<ItemDto> items = itemService.getItems(userId);

        assertThat(items.size(), is(1));
        assertThat(items, is(List.of(itemDto)));
        verify(itemRepository, times(1)).findByUserId(userId);
        verify(itemMapper, times(1)).toDtoList(List.of(item));
    }

    @Test
    void addNewItem_WithDifferentResolvedUrl() {
        long userId = 1;
        String url = "url";
        String resolvedUrl = "resolved url";
        Set<String> tags = Set.of("tag1", "tag2");
        PostItemDto postItemDto = new PostItemDto(url, tags);
        Item item = Item.builder()
                .url(url)
                .tags(tags)
                .build();
        ItemDto itemDto = new ItemDto();
        User user = new User();
        UrlMetadataRetriever.UrlMetadata metadata = UrlMetadataRetrieverImpl.UrlMetadataImpl
                .builder()
                .title("title")
                .hasImage(true)
                .hasVideo(true)
                .resolvedUrl(resolvedUrl)
                .dateResolved(Instant.now())
                .mimeType("text")
                .build();
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(itemMapper.toModel(postItemDto))
                .thenReturn(item);
        when(urlMetadataRetriever.retrieve(url))
                .thenReturn(metadata);
        when(itemRepository.findByResolvedUrl(resolvedUrl))
                .thenReturn(Optional.empty());
        when(itemRepository.save(any()))
                .thenReturn(item);
        when(itemMapper.toDto(item))
                .thenReturn(itemDto);

        itemService.addNewItem(userId, postItemDto);

        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item captorValue = itemArgumentCaptor.getValue();

        assertThat(captorValue.getResolvedUrl(), is(resolvedUrl));
        assertThat(captorValue.getTags(), is(tags));
        assertThat(captorValue.getHasImage(), is(metadata.isHasImage()));
        assertThat(captorValue.getHasVideo(), is(metadata.isHasVideo()));
        assertThat(captorValue.getMimeType(), is(metadata.getMimeType()));
        assertThat(captorValue.getTitle(), is(metadata.getTitle()));
        assertThat(captorValue.getUser(), is(user));
        verify(itemMapper, times(1)).toModel(postItemDto);
        verify(urlMetadataRetriever, times(1)).retrieve(url);
        verify(itemRepository, times(1)).findByResolvedUrl(resolvedUrl);
        verify(itemRepository, times(1)).save(any());
        verify(itemMapper, times(1)).toDto(item);
    }

    @Test
    void addNewItem_WithSameResolvedUrl() {
        long userId = 1;
        String url = "url";
        String resolvedUrl = "resolved url";
        Set<String> tags = Sets.newHashSet("tag1", "tag2");
        Set<String> newTags = Sets.newHashSet("tag1", "tag2", "tag3");
        PostItemDto postItemDto = new PostItemDto(url, tags);
        Item item = Item.builder()
                .url(url)
                .tags(newTags)
                .build();
        Item itemToReturn = Item.builder()
                .url(url)
                .resolvedUrl(resolvedUrl)
                .tags(tags)
                .build();
        ItemDto itemDto = new ItemDto();
        User user = new User();
        UrlMetadataRetriever.UrlMetadata metadata = UrlMetadataRetrieverImpl.UrlMetadataImpl
                .builder()
                .title("title")
                .hasImage(true)
                .hasVideo(true)
                .resolvedUrl(resolvedUrl)
                .dateResolved(Instant.now())
                .mimeType("text")
                .build();
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(itemMapper.toModel(postItemDto))
                .thenReturn(item);
        when(urlMetadataRetriever.retrieve(url))
                .thenReturn(metadata);
        when(itemRepository.findByResolvedUrl(resolvedUrl))
                .thenReturn(Optional.of(itemToReturn));
        when(itemRepository.save(any()))
                .thenReturn(item);
        when(itemMapper.toDto(item))
                .thenReturn(itemDto);

        itemService.addNewItem(userId, postItemDto);

        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item captorValue = itemArgumentCaptor.getValue();

        assertThat(captorValue.getResolvedUrl(), is(resolvedUrl));
        assertThat(captorValue.getTags(), is(newTags));
        assertThat(captorValue.getHasImage(), is(item.getHasImage()));
        assertThat(captorValue.getHasVideo(), is(item.getHasVideo()));
        assertThat(captorValue.getMimeType(), is(item.getMimeType()));
        assertThat(captorValue.getTitle(), is(item.getTitle()));
        assertThat(captorValue.getUser(), is(item.getUser()));
        verify(itemMapper, times(1)).toModel(postItemDto);
        verify(urlMetadataRetriever, times(1)).retrieve(url);
        verify(itemRepository, times(1)).findByResolvedUrl(resolvedUrl);
        verify(itemRepository, times(1)).save(any());
        verify(itemMapper, times(1)).toDto(item);
    }

    @Test
    void addNewItem_NotFoundUser() {
        long userId = 1;
        String url = "url";
        Set<String> tags = Sets.newHashSet("tag1", "tag2");
        PostItemDto postItemDto = new PostItemDto(url, tags);
        Item item = Item.builder()
                .url(url)
                .tags(tags)
                .build();

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        NoSuchElementException e = assertThrows(NoSuchElementException.class,
                () -> itemService.addNewItem(userId, postItemDto));
        assertThat(e.getMessage(), is("Пользователь с id '" + userId + "' не найден."));
        verify(itemMapper, never()).toModel((ItemDto) any());
        verify(urlMetadataRetriever, never()).retrieve(any());
        verify(itemRepository, never()).save(any());
        verify(itemMapper, never()).toDto(any());
    }

    @Test
    void deleteItem_UserHavePermission() {
        long userId = 1;
        long itemId = 2;
        User user = User.builder().id(userId).build();
        Item item = Item.builder().user(user).build();
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));

        itemService.deleteItem(userId, itemId);

        verify(itemRepository, times(1)).deleteByUserIdAndId(userId, itemId);
    }

    @Test
    void deleteItem_UserDoesNotHavePermission() {
        long userId = 1;
        long ownerId = 2;
        long itemId = 2;
        User user = User.builder().id(userId).build();
        User owner = User.builder().id(ownerId).build();
        Item item = Item.builder().user(owner).build();
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));

        NotAuthorizedException e = assertThrows(NotAuthorizedException.class,
                () -> itemService.deleteItem(userId, itemId));

        assertThat(e.getMessage(), is("Неавторизованный доступ!"));
        verify(itemRepository, never()).deleteByUserIdAndId(anyLong(), anyLong());
    }

    @Test
    void deleteItem_UserNotFound() {
        long userId = 1;
        long itemId = 2;
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        NoSuchElementException e = assertThrows(NoSuchElementException.class,
                () -> itemService.deleteItem(userId, itemId));

        assertThat(e.getMessage(), is("Пользователь с id '" + userId + "' не найден."));
        verify(itemRepository, never()).findById(anyLong());
        verify(itemRepository, never()).deleteByUserIdAndId(anyLong(), anyLong());
    }

    @Test
    void deleteItem_ItemNotFound() {
        long userId = 1;
        long itemId = 2;
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(new User()));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty());

        NoSuchElementException e = assertThrows(NoSuchElementException.class,
                () -> itemService.deleteItem(userId, itemId));

        assertThat(e.getMessage(), is("Ссылка с id '" + itemId + "' не найдена."));
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, never()).deleteByUserIdAndId(anyLong(), anyLong());
    }

    @Test
    void findByUserIdAndTags_ReturnItems() {
        long userId = 1;
        Set<String> tags = Sets.newHashSet("tag1", "tag2");
        Item item = new Item();
        ItemDto itemDto = new ItemDto();
        when(itemRepository.findByUserAndTags(userId, tags))
                .thenReturn(List.of(item));
        when(itemMapper.toDtoList(List.of(item)))
                .thenReturn(List.of(itemDto));

        List<ItemDto> items = itemService.findByUserIdAndTags(userId, tags);

        assertThat(items, is(List.of(itemDto)));
        verify(itemRepository, times(1)).findByUserAndTags(userId, tags);
        verify(itemMapper, times(1)).toDtoList(List.of(item));
    }

    @Test
    void searchItems_StateReadContentImageSortNewest() {
        long userId = 1;
        GetItemRequest.State state = GetItemRequest.State.READ;
        GetItemRequest.ContentType contentType = GetItemRequest.ContentType.IMAGE;
        List<String> tags = Lists.newArrayList("tag1", "tag2");
        GetItemRequest.Sort sort = GetItemRequest.Sort.NEWEST;
        int limit = 4;
        GetItemRequest getItemRequest = GetItemRequest.builder()
                .userId(userId)
                .state(state)
                .contentType(contentType)
                .tags(tags)
                .sort(sort)
                .limit(limit)
                .build();
        BooleanExpression condition = item.user.id.eq(userId)
                .and(item.unread.isFalse())
                .and(item.mimeType.eq("image"))
                .and(item.tags.any().in(tags));
        Sort sortBy = Sort.by(Sort.Direction.ASC, "dateResolved");
        PageRequest pageRequest = PageRequest.of(0, limit, sortBy);
        Page<Item> findAll = Page.empty();
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(new User()));
        when(itemRepository.findAll(eq(condition), eq(pageRequest)))
                .thenReturn((findAll));
        when(itemMapper.toDtoList(Lists.newArrayList(findAll)))
                .thenReturn(anyList());

        itemService.searchItems(getItemRequest);

        verify(itemRepository, times(1))
                .findAll(booleanExpressionArgumentCaptor.capture(), eq(pageRequest));
        BooleanExpression captorValue = booleanExpressionArgumentCaptor.getValue();
        assertThat(captorValue, is(condition));
    }

    @Test
    void searchItems_StateUnreadContentVideoSortOldest() {
        long userId = 1;
        GetItemRequest.State state = GetItemRequest.State.UNREAD;
        GetItemRequest.ContentType contentType = GetItemRequest.ContentType.VIDEO;
        List<String> tags = Lists.newArrayList("tag1", "tag2");
        GetItemRequest.Sort sort = GetItemRequest.Sort.OLDEST;
        int limit = 4;
        GetItemRequest getItemRequest = GetItemRequest.builder()
                .userId(userId)
                .state(state)
                .contentType(contentType)
                .tags(tags)
                .sort(sort)
                .limit(limit)
                .build();
        BooleanExpression condition = item.user.id.eq(userId)
                .and(item.unread.isTrue())
                .and(item.mimeType.eq("video"))
                .and(item.tags.any().in(tags));
        Sort sortBy = Sort.by(Sort.Direction.DESC, "dateResolved");
        PageRequest pageRequest = PageRequest.of(0, limit, sortBy);
        Page<Item> findAll = Page.empty();
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(new User()));
        when(itemRepository.findAll(eq(condition), eq(pageRequest)))
                .thenReturn((findAll));
        when(itemMapper.toDtoList(Lists.newArrayList(findAll)))
                .thenReturn(anyList());

        itemService.searchItems(getItemRequest);

        verify(itemRepository, times(1))
                .findAll(booleanExpressionArgumentCaptor.capture(), eq(pageRequest));
        BooleanExpression captorValue = booleanExpressionArgumentCaptor.getValue();
        assertThat(captorValue, is(condition));
    }

    @Test
    void searchItems_StateUnreadContentArticleSortTitle() {
        long userId = 1;
        GetItemRequest.State state = GetItemRequest.State.UNREAD;
        GetItemRequest.ContentType contentType = GetItemRequest.ContentType.ARTICLE;
        List<String> tags = Lists.newArrayList("tag1", "tag2");
        GetItemRequest.Sort sort = GetItemRequest.Sort.TITLE;
        int limit = 4;
        GetItemRequest getItemRequest = GetItemRequest.builder()
                .userId(userId)
                .state(state)
                .contentType(contentType)
                .tags(tags)
                .sort(sort)
                .limit(limit)
                .build();
        BooleanExpression condition = item.user.id.eq(userId)
                .and(item.unread.isTrue())
                .and(item.mimeType.eq("text"))
                .and(item.tags.any().in(tags));
        Sort sortBy = Sort.by(Sort.Direction.ASC, "title");
        PageRequest pageRequest = PageRequest.of(0, limit, sortBy);
        Page<Item> findAll = Page.empty();
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(new User()));
        when(itemRepository.findAll(eq(condition), eq(pageRequest)))
                .thenReturn((findAll));
        when(itemMapper.toDtoList(Lists.newArrayList(findAll)))
                .thenReturn(anyList());

        itemService.searchItems(getItemRequest);

        verify(itemRepository, times(1))
                .findAll(booleanExpressionArgumentCaptor.capture(), eq(pageRequest));
        BooleanExpression captorValue = booleanExpressionArgumentCaptor.getValue();
        assertThat(captorValue, is(condition));
    }

    @Test
    void searchItems_StateAllContentAllSortTitleTagsNull() {
        long userId = 1;
        GetItemRequest.State state = GetItemRequest.State.ALL;
        GetItemRequest.ContentType contentType = GetItemRequest.ContentType.ALL;
        GetItemRequest.Sort sort = GetItemRequest.Sort.TITLE;
        int limit = 4;
        GetItemRequest getItemRequest = GetItemRequest.builder()
                .userId(userId)
                .state(state)
                .contentType(contentType)
                .sort(sort)
                .limit(limit)
                .build();
        BooleanExpression condition = item.user.id.eq(userId);
        Sort sortBy = Sort.by(Sort.Direction.ASC, "title");
        PageRequest pageRequest = PageRequest.of(0, limit, sortBy);
        Page<Item> findAll = Page.empty();
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(new User()));
        when(itemRepository.findAll(eq(condition), eq(pageRequest)))
                .thenReturn((findAll));
        when(itemMapper.toDtoList(Lists.newArrayList(findAll)))
                .thenReturn(anyList());

        itemService.searchItems(getItemRequest);

        verify(itemRepository, times(1))
                .findAll(booleanExpressionArgumentCaptor.capture(), eq(pageRequest));
        BooleanExpression captorValue = booleanExpressionArgumentCaptor.getValue();
        assertThat(captorValue, is(condition));
    }

    @Test
    void searchItems_StateAllContentAllSortTitleTagsEmptyList() {
        long userId = 1;
        GetItemRequest.State state = GetItemRequest.State.ALL;
        GetItemRequest.ContentType contentType = GetItemRequest.ContentType.ALL;
        GetItemRequest.Sort sort = GetItemRequest.Sort.TITLE;
        int limit = 4;
        List<String> tags = Collections.emptyList();
        GetItemRequest getItemRequest = GetItemRequest.builder()
                .userId(userId)
                .state(state)
                .contentType(contentType)
                .sort(sort)
                .limit(limit)
                .tags(tags)
                .build();
        BooleanExpression condition = item.user.id.eq(userId);
        Sort sortBy = Sort.by(Sort.Direction.ASC, "title");
        PageRequest pageRequest = PageRequest.of(0, limit, sortBy);
        Page<Item> findAll = Page.empty();
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(new User()));
        when(itemRepository.findAll(eq(condition), eq(pageRequest)))
                .thenReturn((findAll));
        when(itemMapper.toDtoList(Lists.newArrayList(findAll)))
                .thenReturn(anyList());

        itemService.searchItems(getItemRequest);

        verify(itemRepository, times(1))
                .findAll(booleanExpressionArgumentCaptor.capture(), eq(pageRequest));
        BooleanExpression captorValue = booleanExpressionArgumentCaptor.getValue();
        assertThat(captorValue, is(condition));
    }

    @Test
    void searchItems_UserNotFound() {
        long userId = 1;
        GetItemRequest getItemRequest = GetItemRequest.builder().userId(userId).build();
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        NoSuchElementException e = assertThrows(NoSuchElementException.class,
                () -> itemService.searchItems(getItemRequest));
        assertThat(e.getMessage(), is("Пользователь с id '" + userId + "' не найден."));
        verify(itemRepository, never()).findAll(Mockito.any(BooleanExpression.class),
                Mockito.any(PageRequest.class));
        verify(itemMapper, never()).toDtoList(anyList());
    }

    @Test
    void modifyItem_ReplaceTagsUserHavePermission() {
        long userId = 1;
        long itemId = 2;
        User user = User.builder().id(userId).build();
        Set<String> tags = Sets.newHashSet("tag1", "tag2");
        Item item = Item.builder().user(user).tags(tags).build();
        List<String> replaceTags = Lists.newArrayList("tag3", "tag4");
        ModifyItemRequest modifyItemRequest = ModifyItemRequest.builder()
                .itemId(itemId)
                .replaceTags(true)
                .unread(false)
                .tags(replaceTags)
                .build();
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(item);
        when(itemMapper.toDto(item))
                .thenReturn(Mockito.any(ItemDto.class));

        itemService.modifyItem(userId, modifyItemRequest);

        verify(itemMapper, times(1)).toDto(item);
        verify(itemRepository, times(1)).save(itemArgumentCaptor.capture());
        Item captorValue = itemArgumentCaptor.getValue();
        assertThat(captorValue.getTags(), containsInAnyOrder("tag3", "tag4"));
        assertThat(captorValue.getTags(), not(containsInAnyOrder("tag1", "tag2")));
        assertThat(captorValue.isUnread(), is(modifyItemRequest.isUnread()));
        assertThat(captorValue.getUser(), is(user));
    }

    @Test
    void modifyItem_AddTagsUserHavePermission() {
        long userId = 1;
        long itemId = 2;
        User user = User.builder().id(userId).build();
        Set<String> tags = Sets.newHashSet("tag1", "tag2");
        Item item = Item.builder().user(user).tags(tags).build();
        List<String> replaceTags = Lists.newArrayList("tag3", "tag4");
        ModifyItemRequest modifyItemRequest = ModifyItemRequest.builder()
                .itemId(itemId)
                .replaceTags(false)
                .unread(false)
                .tags(replaceTags)
                .build();
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(item);
        when(itemMapper.toDto(item))
                .thenReturn(Mockito.any(ItemDto.class));

        itemService.modifyItem(userId, modifyItemRequest);

        verify(itemMapper, times(1)).toDto(item);
        verify(itemRepository, times(1)).save(itemArgumentCaptor.capture());
        Item captorValue = itemArgumentCaptor.getValue();
        assertThat(captorValue.getTags(), containsInAnyOrder("tag1", "tag2", "tag3", "tag4"));
        assertThat(captorValue.isUnread(), is(modifyItemRequest.isUnread()));
        assertThat(captorValue.getUser(), is(user));
    }

    @Test
    void modifyItem_UserNotFound() {
        long userId = 1;
        long itemId = 2;
        List<String> replaceTags = Lists.newArrayList("tag3", "tag4");
        ModifyItemRequest modifyItemRequest = ModifyItemRequest.builder()
                .itemId(itemId)
                .replaceTags(false)
                .unread(false)
                .tags(replaceTags)
                .build();
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        NoSuchElementException e = assertThrows(NoSuchElementException.class,
                () -> itemService.modifyItem(userId, modifyItemRequest));
        assertThat(e.getMessage(), is("Пользователь с id '" + userId + "' не найден."));

        verify(itemMapper, never()).toDto(Mockito.any(Item.class));
        verify(itemRepository, never()).save(Mockito.any(Item.class));
    }

    @Test
    void modifyItem_ItemNotFound() {
        long userId = 1;
        long itemId = 2;
        User user = User.builder().id(userId).build();
        List<String> replaceTags = Lists.newArrayList("tag3", "tag4");
        ModifyItemRequest modifyItemRequest = ModifyItemRequest.builder()
                .itemId(itemId)
                .replaceTags(false)
                .unread(false)
                .tags(replaceTags)
                .build();
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty());

        NoSuchElementException e = assertThrows(NoSuchElementException.class,
                () -> itemService.modifyItem(userId, modifyItemRequest));
        assertThat(e.getMessage(), is("Ссылка с id '" + itemId + "' не найдена."));

        verify(itemMapper, never()).toDto(Mockito.any(Item.class));
        verify(itemRepository, never()).save(Mockito.any(Item.class));
    }

    @Test
    void modifyItem_UserNotHavePermission() {
        long userId = 1;
        long itemId = 2;
        long ownerId = 3;
        User user = User.builder().id(userId).build();
        User owner = User.builder().id(ownerId).build();
        Set<String> tags = Sets.newHashSet("tag1", "tag2");
        Item item = Item.builder().user(owner).tags(tags).build();
        List<String> replaceTags = Lists.newArrayList("tag3", "tag4");
        ModifyItemRequest modifyItemRequest = ModifyItemRequest.builder()
                .itemId(itemId)
                .replaceTags(false)
                .unread(false)
                .tags(replaceTags)
                .build();
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));

        NotAuthorizedException e = assertThrows(NotAuthorizedException.class,
                () -> itemService.modifyItem(userId, modifyItemRequest));

        assertThat(e.getMessage(), is("Неавторизованный доступ!"));
        verify(itemMapper, never()).toDto(Mockito.any(Item.class));
        verify(itemRepository, never()).save(Mockito.any(Item.class));
    }
}