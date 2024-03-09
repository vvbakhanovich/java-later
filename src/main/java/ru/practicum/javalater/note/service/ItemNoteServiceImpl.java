package ru.practicum.javalater.note.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.javalater.item.entity.Item;
import ru.practicum.javalater.item.repository.ItemRepository;
import ru.practicum.javalater.note.dto.ItemNoteDto;
import ru.practicum.javalater.note.entity.ItemNote;
import ru.practicum.javalater.note.mapper.ItemNoteMapper;
import ru.practicum.javalater.note.repository.ItemNoteRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemNoteServiceImpl implements ItemNoteService {

    private final ItemNoteRepository itemNoteRepository;

    private final ItemNoteMapper itemNoteMapper;

    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemNoteDto addNewItemNote(long userId, ItemNoteDto itemNoteDto) {
        Item item = itemRepository.findById(itemNoteDto.getItemId())
                .orElseThrow(() -> new NoSuchElementException("Item not found"));
        ItemNote itemNote = itemNoteMapper.toModel(itemNoteDto);
        itemNote.setItem(item);
        ItemNote savedNote = itemNoteRepository.save(itemNote);
        log.info("Добавлена записка с id '{}'.", savedNote.getId());
        return itemNoteMapper.toDto(savedNote);
    }

    @Override
    public List<ItemNoteDto> searchNotesByUrl(String url, Long userId) {
        List<ItemNote> notes = itemNoteRepository.findAllByItemUserIdAndItemUrlContaining(userId, url);
        log.info("Пользователь с id '{}' ищет заметки с url '{}'.", userId, url);
        return itemNoteMapper.toDtoList(notes);
    }

    @Override
    public List<ItemNoteDto> searchNotesByTag(long userId, String tag) {
        List<ItemNote> notes = itemNoteRepository.findAllByItemUserIdAndItemTag(userId, tag);
        log.info("Пользователь с id '{}' ищет заметки с тэгом '{}'.", userId, tag);
        return itemNoteMapper.toDtoList(notes);
    }

    @Override
    public List<ItemNoteDto> listAllItemsWithNotes(long userId, int from, int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<ItemNote> notes = itemNoteRepository.findAllByItemUserId(userId, page).getContent();
        log.info("Получение списка всех заметок с '{}' элемента по '{}' на странице.", from, size);
        return itemNoteMapper.toDtoList(notes);
    }
}
