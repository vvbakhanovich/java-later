package ru.practicum.javalater.note.service;

import ru.practicum.javalater.note.dto.ItemNoteDto;

import javax.transaction.Transactional;
import java.util.List;

public interface ItemNoteService {

    @Transactional
    ItemNoteDto addNewItemNote(long userId, ItemNoteDto itemNoteDto);

    List<ItemNoteDto> searchNotesByUrl(String url, Long userId);

    List<ItemNoteDto> searchNotesByTag(long userId, String tag);

    List<ItemNoteDto> listAllItemsWithNotes(long userId, int from, int size);
}
