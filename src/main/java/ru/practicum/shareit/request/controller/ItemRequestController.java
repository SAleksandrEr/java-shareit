package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.GetItemRequestParam;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @Transactional
    @PostMapping
    public ItemRequestResponse createItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @Valid @RequestBody ItemRequestDto itemRequestDto) {
        if (userId != null) {
            return itemRequestService.createItemRequest(userId, itemRequestDto);
        } else {
            throw new ValidationException("Invalid data - userId");
        }
    }

    @GetMapping
    public List<ItemRequestResponse.ItemRequestResponseItems> findIItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId) {
        if (userId != null) {
            return itemRequestService.findItemRequest(userId);
        } else {
            throw new ValidationException("Invalid date - userId");
        }
    }

    @GetMapping("/all")
    public List<ItemRequestResponse.ItemRequestResponseItems> findIItemRequestAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestParam(defaultValue = "0", required = false) int from,
                                          @RequestParam(defaultValue = "10", required = false) int size) {
        if (from < 0 || size < 1) {
            throw new ValidationException("Param - <from> or <size> is not correct");
        }
        return itemRequestService.findItemRequestAll(userId, GetItemRequestParam.pageRequest(from, size));
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponse.ItemRequestResponseItems findItemRequestId(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("requestId") Long requestId) {
        return itemRequestService.findItemRequestId(userId, requestId);
    }
}
