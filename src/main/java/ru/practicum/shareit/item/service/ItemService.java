package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoPatch;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemService {

    private final ItemStorage itemStorage;

    private final UserService userService;

    private final ItemMapper itemMapper;

    @Autowired
    public ItemService(@Qualifier("itemDaoImpl") ItemStorage itemStorage, UserService userService, ItemMapper itemMapper) {
        this.itemStorage = itemStorage;
        this.userService = userService;
        this.itemMapper = itemMapper;
    }

    public ItemDtoResponse createItem(Long userId, ItemDto itemDto) {
        Item item = itemMapper.toItem(userId, itemDto);
        userService.findUsersId(item.getOwner());
        return itemMapper.toItemDtoResponse(itemStorage.createItem(item));
    }

    public ItemDtoResponse updateItem(Long userId, ItemDtoPatch itemDtoPatch) {
        Item item = itemMapper.toItemDtoPatch(userId, itemDtoPatch);
        userService.findUsersId(item.getOwner());
        Item oldItem = itemStorage.getItemId(item.getId());
        if (item.getOwner().equals(oldItem.getOwner())) {
            if (item.getName() != null) {
                oldItem.setName(item.getName());
            }
            if (item.getDescription() != null) {
                oldItem.setDescription(item.getDescription());
            }
            if (item.getAvailable() != null) {
                oldItem.setAvailable(item.getAvailable());
            }
            log.info("Вещь обновлена " + oldItem);
            return itemMapper.toItemDtoResponse(itemStorage.updateItem(oldItem));
        } else {
            throw new ValidationException("Updating is not possible for the user - '" + userId +
                    "' available only to the owner of the item");
        }
    }

    public ItemDtoResponse getItemId(Long userId, Long id) {
        userService.findUsersId(userId);
        Item item = itemStorage.getItemId(id);
        log.info("Получена вещь с id " + id);
        return itemMapper.toItemDtoResponse(item);
    }

    public List<ItemDtoResponse> findItemsByUserId(Long id) {
        userService.findUsersId(id);
        List<Item> items = itemStorage.findItemsByUserId(id);
        log.info("Получены вещи пользователя с id " + id);
        return items.stream()
                .map(itemMapper::toItemDtoResponse)
                .collect(Collectors.toList());
    }

    public List<ItemDtoResponse> searchNameItemsAndDescription(String query) {
        if (query.isEmpty()) {
            return new ArrayList<>();
        }
        List<Item> items = itemStorage.searchNameItemsAndDescription(query);
        log.info("Найдены вещи по запросу - " + query);
        return items.stream()
                .map(itemMapper::toItemDtoResponse)
                .collect(Collectors.toList());
    }
}
