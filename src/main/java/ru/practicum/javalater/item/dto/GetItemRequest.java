package ru.practicum.javalater.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetItemRequest {

    private Long userId;

    private State state;

    private ContentType contentType;

    private List<String> tags;

    private Sort sort;

    private Integer limit;

    public static GetItemRequest of(long userId,
                                    String state,
                                    String contentType,
                                    String sort,
                                    int limit,
                                    List<String> tags) {
        GetItemRequest request = new GetItemRequest();
        request.setUserId(userId);
        request.setLimit(limit);
        request.setState(State.valueOf(state.toUpperCase()));
        request.setContentType(ContentType.valueOf(contentType.toUpperCase()));
        request.setSort(Sort.valueOf(sort.toUpperCase()));
        if(tags != null) {
            request.setTags(tags);
        }
        return request;
    }

    public boolean hasTags() {
        return tags != null && !tags.isEmpty();
    }

    public enum State {
        ALL, UNREAD, READ
    }

    public enum ContentType {
        ALL, ARTICLE, IMAGE, VIDEO
    }

    public enum Sort {
        NEWEST, OLDEST, TITLE
    }
}
