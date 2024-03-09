package ru.practicum.javalater.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {

    private Long id;

    private Long userId;

    private String url;

    private Set<String> tags;

    private String resolvedUrl;

    private String mimeType;

    private String title;

    private boolean hasImage;

    private boolean hasVideo;

    private Instant dateResolved;

    private boolean unread;
}
