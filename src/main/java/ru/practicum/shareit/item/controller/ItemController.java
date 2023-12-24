package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoPatch;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @Autowired
    public ItemController(ItemService itemService, ItemMapper itemMapper) {
        this.itemService = itemService;
        this.itemMapper = itemMapper;
    }

    @PostMapping
    public ItemDtoResponse createUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @Valid @RequestBody ItemDto itemDto) {
        if (userId != null) {
            Item modifiItem = itemService.createItem(itemMapper.toItem(userId, itemDto));
            return itemMapper.toItemDtoResponse(modifiItem);
        } else {
            throw new ValidationException("Invalid date - userId");
        }
    }

    @PatchMapping("/{itemId}")
    public ItemDtoResponse updateItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                    @Valid @RequestBody ItemDtoPatch item) {
            item.setId(itemId);
            Item modifiItem = itemService.updateItem(itemMapper.toItemDtoPatch(userId, item));
            return itemMapper.toItemDtoResponse(modifiItem);
    }

    @GetMapping("/{itemId}")
    public ItemDtoResponse findItemId(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("itemId") Long itemId) {
            Item modifiItem = itemService.getItemId(userId, itemId);
            return itemMapper.toItemDtoResponse(modifiItem);
    }

    @GetMapping
    public List<ItemDtoResponse> findItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        if (userId != null) {
            return itemService.findItemsByUserId(userId).stream()
                    .map(itemMapper::toItemDtoResponse)
                    .collect(Collectors.toList());
        } else {
            throw new ValidationException("Invalid date - userId or itemId");
        }
    }

    @GetMapping("/search")
    public List<ItemDtoResponse> searchNameItemsAndDescription(@RequestParam(value = "text", required = false) String query) {
        return itemService.searchNameItemsAndDescription(query).stream()
                .map(itemMapper::toItemDtoResponse)
                .collect(Collectors.toList());
    }
}

