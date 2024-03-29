package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepositoryJpa;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepositoryJPA;
import ru.practicum.shareit.user.storage.UserRepositoryJpa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
public class ItemRequestService {

    private final ItemRequestMapper itemRequestMapper;
    private final UserRepositoryJpa userRepositoryJpa;
    private final ItemRequestRepositoryJPA itemRequestRepositoryJPA;
    private final ItemRepositoryJpa itemRepositoryJpa;
    private final ItemMapper itemMapper;

    @Transactional
    public ItemRequestResponse createItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(userRepositoryJpa.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found " + userId)));
        return itemRequestMapper.toItemRequestResponse((itemRequestRepositoryJPA.save(itemRequest)));
    }

    public List<ItemRequestResponse.ItemRequestResponseItems> findItemRequest(Long userId) {
        userRepositoryJpa.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found " + userId));
        List<ItemRequest> itemRequestList = itemRequestRepositoryJPA.findAllByRequestorIdOrderByCreatedDesc(userId);
        List<Long> list = itemRequestList.stream().map(ItemRequest::getId).collect(Collectors.toList());
        List<Item> items = itemRepositoryJpa.findAllByRequestIdInOrderByIdDesc(list);
            Map<Long, ItemDtoResponse.ItemDtoResponseRequest> itemsRequestList = items.stream()
                    .map(itemMapper::toItemDtoResponseRequest).collect(Collectors
                            .toMap(ItemDtoResponse.ItemDtoResponseRequest::getRequestId, Function.identity()));
        List<ItemDtoResponse.ItemDtoResponseRequest> listItemsRequest = new ArrayList<>();
        return itemRequestList.stream()
         .map(itemRequestMapper::toItemRequestResponseItems).peek(itemRequest -> {
                    if (!items.isEmpty()) {
                        listItemsRequest.add(itemsRequestList.get(itemRequest.getId()));
                    }
                    itemRequest.setItems(listItemsRequest);
                }).collect(Collectors.toList());
    }

    public List<ItemRequestResponse.ItemRequestResponseItems> findItemRequestAll(Long userId, Pageable page) {
        userRepositoryJpa.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found " + userId));
        List<ItemRequest> itemRequestList = itemRequestRepositoryJPA.findAllByRequestorIdNotOrderByCreatedDesc(userId, page).getContent();
        List<Long> list = itemRequestList.stream().map(ItemRequest::getId).collect(Collectors.toList());
        List<Item> items = itemRepositoryJpa.findAllByRequestIdInOrderByIdDesc(list);
        Map<Long, ItemDtoResponse.ItemDtoResponseRequest> itemsRequestList = items.stream()
                .map(itemMapper::toItemDtoResponseRequest).collect(Collectors
                        .toMap(ItemDtoResponse.ItemDtoResponseRequest::getRequestId, Function.identity()));
        List<ItemDtoResponse.ItemDtoResponseRequest> listItemsRequest = new ArrayList<>();
        return itemRequestList.stream()
                .map(itemRequestMapper::toItemRequestResponseItems).peek(itemRequest -> {
                    if (!items.isEmpty()) {
                        listItemsRequest.add(itemsRequestList.get(itemRequest.getId()));
                    }
                    itemRequest.setItems(listItemsRequest);
                }).collect(Collectors.toList());
    }

    public ItemRequestResponse.ItemRequestResponseItems findItemRequestId(Long userId, Long requestId) {
        userRepositoryJpa.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found " + userId));
        ItemRequest itemRequest = itemRequestRepositoryJPA.findById(requestId)
                .orElseThrow(() -> new DataNotFoundException("ItemRequest not found " + requestId));
        List<Long> list = Collections.singletonList(itemRequest.getId());
        List<Item> items = itemRepositoryJpa.findAllByRequestIdInOrderByIdDesc(list);
        List<ItemDtoResponse.ItemDtoResponseRequest> itemsRequestList = items.stream()
                .map(itemMapper::toItemDtoResponseRequest).collect(Collectors.toList());
        ItemRequestResponse.ItemRequestResponseItems itemRequestResponseItems = itemRequestMapper.toItemRequestResponseItems(itemRequest);
        itemRequestResponseItems.setItems(itemsRequestList);
        return itemRequestResponseItems;
 }
}
