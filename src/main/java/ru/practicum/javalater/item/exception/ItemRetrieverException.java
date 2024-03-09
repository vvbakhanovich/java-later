package ru.practicum.javalater.item.exception;

import java.net.URISyntaxException;

public class ItemRetrieverException extends RuntimeException {
    public ItemRetrieverException(String message, Throwable cause) {
        super(message, cause);
    }

    public ItemRetrieverException(String message) {
        super(message);
    }
}
