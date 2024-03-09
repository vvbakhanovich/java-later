package ru.practicum.javalater.note.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.javalater.note.entity.ItemNote;

import java.util.List;

public interface ItemNoteRepository extends JpaRepository<ItemNote, Long> {
    List<ItemNote> findAllByItemUserIdAndItemUrlContaining(Long userId, String url);

    @Query("SELECT note FROM ItemNote AS note JOIN note.item AS item WHERE item.user.id = ?1 and ?2 MEMBER OF item.tags")
    List<ItemNote> findAllByItemUserIdAndItemTag(Long userId, String tag);

    Page<ItemNote> findAllByItemUserId(Long userId, Pageable page);
}
