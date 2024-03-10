package ru.practicum.javalater.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.practicum.javalater.item.dto.GetItemRequest;
import ru.practicum.javalater.item.dto.ItemDto;
import ru.practicum.javalater.item.dto.ModifyItemRequest;
import ru.practicum.javalater.item.dto.PostItemDto;
import ru.practicum.javalater.item.service.ItemService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @MockBean
    ItemService itemService;

    @Captor
    ArgumentCaptor<GetItemRequest> getItemRequestArgumentCaptor;

    @Test
    @SneakyThrows
    public void add_shouldReturnStatusOk() {
        long userId = 1;
        PostItemDto postItemDto = new PostItemDto();
        ItemDto itemDto = new ItemDto();
        when(itemService.addNewItem(userId, postItemDto)).thenReturn(itemDto);

        String result = mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Later-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result, is(mapper.writeValueAsString(itemDto)));
        verify(itemService, times(1)).addNewItem(userId, postItemDto);
    }

    @Test
    @SneakyThrows
    public void delete_ShouldReturnStatusOk() {
        long userId = 1;
        long itemId = 2;

        mvc.perform(delete("/items/{itemId}", itemId)
                        .header("X-Later-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemService, times(1)).deleteItem(userId, itemId);
    }

    @Test
    @SneakyThrows
    public void get_withAllFields() {
        long userId = 1;
        String state = "read";
        String contentType = "image";
        String sort = "title";
        int limit = 2;
        List<String> tags = List.of("tag1", "tag2");
        GetItemRequest getItemRequest = GetItemRequest.of(userId, state, contentType, sort, limit, tags);
        ItemDto itemDto = new ItemDto();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("tags", tags);
        when(itemService.searchItems(any()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items")
                        .header("X-Later-User-Id", userId)
                        .param("state", state)
                        .param("contentType", contentType)
                        .param("sort", sort)
                        .param("limit", String.valueOf(limit))
                        .params(params))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(mapper.writeValueAsString(List.of(itemDto))));

        verify(itemService).searchItems(getItemRequestArgumentCaptor.capture());
        GetItemRequest captorValue = getItemRequestArgumentCaptor.getValue();

        assertThat(getItemRequest, is(captorValue));
    }

    @Test
    @SneakyThrows
    public void get_withDefaultFields() {
        long userId = 1;
        String state = "unread";
        String contentType = "all";
        String sort = "newest";
        int limit = 10;
        GetItemRequest getItemRequest = GetItemRequest.of(userId, state, contentType, sort, limit, null);
        ItemDto itemDto = new ItemDto();
        when(itemService.searchItems(any()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items")
                        .header("X-Later-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(mapper.writeValueAsString(List.of(itemDto))));

        verify(itemService).searchItems(getItemRequestArgumentCaptor.capture());
        GetItemRequest captorValue = getItemRequestArgumentCaptor.getValue();

        assertThat(getItemRequest, is(captorValue));
    }

    @Test
    @SneakyThrows
    public void findByUserIdAndTags_shouldReturnStatus200() {
        long userid = 1;
        Set<String> tags = Set.of("tag1", "tag2");
        ItemDto itemDto = new ItemDto();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("tags", new ArrayList<>(tags));
        when(itemService.findByUserIdAndTags(userid, tags))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/find")
                        .header("X-Later-User-Id", userid)
                        .params(params))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(mapper.writeValueAsString(List.of(itemDto))));

        verify(itemService, times(1)).findByUserIdAndTags(userid, tags);
    }

    @Test
    @SneakyThrows
    public void modifyItem_shouldReturnStatusOk() {
        long userId = 1;
        ItemDto itemDto = new ItemDto();
        long itemId = 2;
        boolean unread = false;
        List<String> tags = List.of("tag1", "tag2");
        boolean replaceTags = false;
        ModifyItemRequest modifyItemRequest = new ModifyItemRequest(itemId, unread, tags, replaceTags);
        when(itemService.modifyItem(userId, modifyItemRequest))
                .thenReturn(itemDto);

        mvc.perform(patch("/items")
                        .header("X-Later-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(modifyItemRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(mapper.writeValueAsString(itemDto)));

        verify(itemService, times(1)).modifyItem(userId, modifyItemRequest);
    }
}