package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ItemService {

    private final ItemStorage itemStorage;

    private final UserService userService;

    @Autowired
    public ItemService(@Qualifier("itemDaoImpl") ItemStorage itemStorage, UserService userService) {
        this.itemStorage = itemStorage;
        this.userService = userService;
    }

    public Item createItem(Item item) {
        userService.findUsersId(item.getOwner());
        log.info("Вещь создана " + item);
        return itemStorage.createItem(item);
    }

    public Item updateItem(Item item) {
        userService.findUsersId(item.getOwner());
        Item newItem = itemStorage.getItemId(item.getId());
        if (item.getOwner().equals(newItem.getOwner())) {
            if (item.getName() != null) {
                newItem.setName(item.getName());
            }
            if (item.getDescription() != null) {
                newItem.setDescription(item.getDescription());
            }
            if (item.getAvailable() != null) {
                newItem.setAvailable(item.getAvailable());
            }
            log.info("Вещь обновлена " + newItem);
            return itemStorage.updateItem(newItem);

        } else {
            throw new DataNotFoundException("Data not found ");
        }
    }

    public Item getItemId(Long userId, Long id) {
        userService.findUsersId(userId);
        Item item = itemStorage.getItemId(id);
        log.info("Получена вещь с id " + id);
        return item;
    }

    public List<Item> findItemsByUserId(Long id) {
        userService.findUsersId(id);
        List<Item> items = itemStorage.findItemsByUserId(id);
        log.info("Получена вещи пользователя с id " + id);
        return items;
    }

    public List<Item> searchNameItemsAndDescription(String query) {
        if (query.isEmpty()) {
            return new ArrayList<>();
        }
        List<Item> items = itemStorage.searchNameItemsAndDescription(query);
        log.info("Найдены вещи по запросу - " + query);
        return items;
    }
}
