package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoPatch;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDtoResponse createUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @Valid @RequestBody ItemDto itemDto) {
        if (userId != null) {
            return itemService.createItem(userId, itemDto);
        } else {
            throw new ValidationException("Invalid data - userId");
        }
    }

    @PatchMapping("/{itemId}")
    public ItemDtoResponse updateItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                    @Valid @RequestBody ItemDtoPatch item) {
            item.setId(itemId);
            return itemService.updateItem(userId, item);
    }

    @GetMapping("/{itemId}")
    public ItemDtoResponse findItemId(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("itemId") Long itemId) {
            return itemService.getItemId(userId, itemId);
    }

    @GetMapping
    public List<ItemDtoResponse> findItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        if (userId != null) {
            return itemService.findItemsByUserId(userId);
        } else {
            throw new ValidationException("Invalid date - userId or itemId");
        }
    }

    @GetMapping("/search")
    public List<ItemDtoResponse> searchNameItemsAndDescription(@RequestParam(value = "text", required = false) String query) {
        return itemService.searchNameItemsAndDescription(query);
    }
}

