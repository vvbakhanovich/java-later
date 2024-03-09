package ru.practicum.javalater.item.metadata;

import java.time.Instant;

public interface UrlMetadataRetriever {

    UrlMetadata retrieve(String urlString);

    interface UrlMetadata {
        String getNormalUrl();
        String getResolvedUrl();
        String getMimeType();
        String getTitle();
        boolean isHasImage();
        boolean isHasVideo();
        Instant getDateResolved();
    }
}
