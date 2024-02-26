package ru.practicum.shareit.item.storage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Repository
public interface ItemRepositoryJpa extends JpaRepository<Item, Long> {

    @Modifying
    @Query("UPDATE Item " +
            "SET name = ?1, " +
            "description = ?2, " +
            "available = ?3 " +
            "where id = ?4")
    void updateItem(String name, String description,
                    Boolean getAvailable, Long itemId);

    @Query("select it " +
            "from Item as it " +
            "where it.user.id = ?1 " +
            "ORDER BY it.id")
    Page<Item> findItemsByUserId(Long userId, Pageable page);

    @Query("select it " +
            "from Item as it " +
            "where it.available = true " +
            "AND (LOWER(it.name) like concat('%', ?1, '%') " +
            "OR LOWER(it.description) like concat('%', ?1, '%'))")
    Page<Item> findByNameAndDescription(String query, Pageable page);


    Item findByIdAndAvailableTrue(Long id);

    List<Item> findAllByRequestIdInOrderByIdDesc(List<Long> list);

}
