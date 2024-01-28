package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
public class ItemController {

    private final ItemService itemService;

    @Transactional
    @PostMapping
    public ItemDtoResponse createUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @Valid @RequestBody ItemDto itemDto) {
        if (userId != null) {
            return itemService.createItem(userId, itemDto);
        } else {
            throw new ValidationException("Invalid data - userId");
        }
    }

    @Transactional
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

    @Transactional
    @PostMapping("/{itemId}/comment")
    public CommentDtoResponse createCommentItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("itemId") Long itemId,
                                                @Valid @RequestBody CommentDto commentDto) {
        return itemService.createCommentItem(userId, itemId, commentDto);
    }
}

