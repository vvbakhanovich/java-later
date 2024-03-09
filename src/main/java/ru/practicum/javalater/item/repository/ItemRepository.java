package ru.practicum.javalater.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.javalater.item.entity.Item;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item> {

    List<Item> findByUserId(Long userId);

    void deleteByUserIdAndId(Long userId, Long itemId);

    @Query("select item from Item item JOIN item.tags tags WHERE item.user.id = ?1 and tags IN ?2")
    List<Item> findByUserAndTags(Long userId, Set<String> tags);

    Optional<Item> findByResolvedUrl(String resolvedUrl);
}