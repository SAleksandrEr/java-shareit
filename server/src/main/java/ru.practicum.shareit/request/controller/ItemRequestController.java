package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.GetItemRequestParam;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

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
                                                 @RequestBody ItemRequestDto itemRequestDto) {
            return itemRequestService.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestResponse.ItemRequestResponseItems> findIItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId) {
            return itemRequestService.findItemRequest(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponse.ItemRequestResponseItems> findIItemRequestAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestParam int from, @RequestParam int size) {
        return itemRequestService.findItemRequestAll(userId, GetItemRequestParam.pageRequest(from, size));
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponse.ItemRequestResponseItems findItemRequestId(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("requestId") Long requestId) {
        return itemRequestService.findItemRequestId(userId, requestId);
    }
}
