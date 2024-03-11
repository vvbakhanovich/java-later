package ru.practicum.javalater.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor(staticName = "of")
@Builder
public class ModifyItemRequest {

    private long itemId;

    private boolean unread;

    private List<String> tags;

    private boolean replaceTags;

    public boolean hasTags() {
        return tags != null && !tags.isEmpty();
    }
}
