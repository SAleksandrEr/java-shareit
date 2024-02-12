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
                .orElseThrow(() -> new DataNotFoundException("User not found")));
        return itemRequestMapper.toItemRequestResponse((itemRequestRepositoryJPA.save(itemRequest)));
    }

    public List<ItemRequestResponse.ItemRequestResponseItems> findItemRequest(Long userId) {
        userRepositoryJpa.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        // проверяем есть такой пользователь
        // выгружаем все реквесты пользователя
        // выгружаем items реквестов пользователей.....
        List<ItemRequest> itemRequestList = itemRequestRepositoryJPA.findAllByRequestorIdOrderByCreatedDesc(userId);
        List<Long> list = itemRequestList.stream().map(ItemRequest::getId).collect(Collectors.toList());
        List<Item> items = itemRepositoryJpa.findAllByRequestIdInOrderByIdDesc(list);
        List<ItemDtoResponse.ItemDtoResponseRequest> itemsRequestList = items.stream()
                .map(itemMapper::toItemDtoResponseRequest).collect(Collectors.toList());
        List<ItemRequestResponse.ItemRequestResponseItems> itemRequestResponseItems = itemRequestList.stream()
                .map(itemRequestMapper::toItemRequestResponseItems)
                .collect(Collectors.toList());
        List<ItemDtoResponse.ItemDtoResponseRequest> listItemsRequest = new ArrayList<>();
        for (ItemRequestResponse.ItemRequestResponseItems listRequest: itemRequestResponseItems) {

            for (ItemDtoResponse.ItemDtoResponseRequest listItems: itemsRequestList) {
                if (listRequest.getId().equals(listItems.getRequestId())) {
                    listItemsRequest.add(listItems);
                }
            }
            listRequest.setItems(listItemsRequest);
        }
        return itemRequestResponseItems;
    }

    public List<ItemRequestResponse.ItemRequestResponseItems> findItemRequestAll(Long userId, Pageable page) {
        userRepositoryJpa.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        List<ItemRequest> itemRequestList = itemRequestRepositoryJPA.findAllByRequestorIdNotOrderByCreatedDesc(userId, page).getContent();
        List<Long> list = itemRequestList.stream().map(ItemRequest::getId).collect(Collectors.toList());
        List<Item> items = itemRepositoryJpa.findAllByRequestIdInOrderByIdDesc(list);
        List<ItemDtoResponse.ItemDtoResponseRequest> itemsRequestList = items.stream()
                .map(itemMapper::toItemDtoResponseRequest).collect(Collectors.toList());
        List<ItemRequestResponse.ItemRequestResponseItems> itemRequestResponseItems = itemRequestList.stream()
                .map(itemRequestMapper::toItemRequestResponseItems)
                .collect(Collectors.toList());
        List<ItemDtoResponse.ItemDtoResponseRequest> listItemsRequest = new ArrayList<>();
        for (ItemRequestResponse.ItemRequestResponseItems listRequest: itemRequestResponseItems) {
            for (ItemDtoResponse.ItemDtoResponseRequest listItems: itemsRequestList) {
                if (listRequest.getId().equals(listItems.getRequestId())) {
                    listItemsRequest.add(listItems);
                }
            }
            listRequest.setItems(listItemsRequest);
        }
        return itemRequestResponseItems;
    }

public ItemRequestResponse.ItemRequestResponseItems findItemRequestId(Long userId, Long requestId) {
    userRepositoryJpa.findById(userId)
            .orElseThrow(() -> new DataNotFoundException("User not found"));
    ItemRequest itemRequest = itemRequestRepositoryJPA.findById(requestId)
            .orElseThrow(() -> new DataNotFoundException("ItemRequest not found"));
    List<Long> list = Collections.singletonList(itemRequest.getId());
    List<Item> items = itemRepositoryJpa.findAllByRequestIdInOrderByIdDesc(list);
    List<ItemDtoResponse.ItemDtoResponseRequest> itemsRequestList = items.stream()
            .map(itemMapper::toItemDtoResponseRequest).collect(Collectors.toList());
    ItemRequestResponse.ItemRequestResponseItems itemRequestResponseItems = itemRequestMapper.toItemRequestResponseItems(itemRequest);
    itemRequestResponseItems.setItems(itemsRequestList);
    return itemRequestResponseItems;
 }
}
