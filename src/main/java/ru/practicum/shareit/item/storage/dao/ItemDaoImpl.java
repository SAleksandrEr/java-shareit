package ru.practicum.shareit.item.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component("itemDaoImpl")
public class ItemDaoImpl implements ItemStorage {

    private Long generationId = 0L;
    private final Map<Long, Item> itemList = new HashMap<>();

    @Override
    public Item createItem(Item item) {
        item.setId(getGenerationId());
        itemList.put(item.getId(), item);
        log.info("Вещь создана " + item);
        return item;
    }

    @Override
    public Item getItemId(Long id) {
        Item item = itemList.get(id);
        if (item == null) {
            throw new DataNotFoundException("Data not found " + id);
        }
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        itemList.put(item.getId(),item);
        return item;
    }

    @Override
    public List<Item> findItemsByUserId(Long id) {
        return itemList.values()
                .stream()
                .filter(item -> item.getUser().equals(id))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchNameItemsAndDescription(String query) {
        return itemList.values()
                .stream()
                .filter(item -> item.getAvailable().equals(true))
                .filter(item -> item.getName().toLowerCase()
                        .contains(query.toLowerCase()) || item.getDescription().toLowerCase()
                        .contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    private Long getGenerationId() {
        return ++generationId;
    }
}
