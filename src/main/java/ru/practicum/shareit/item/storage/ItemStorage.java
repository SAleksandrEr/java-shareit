package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item createItem(Item item);

    Item getItemId(Long id);

    Item updateItem(Item item);

    List<Item> findItemsByUserId(Long id);

    List<Item> searchNameItemsAndDescription(String query);
}
