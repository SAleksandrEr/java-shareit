package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.model.ItemRequest;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {

    ItemRequest toItemRequest(ItemRequestDto itemRequestDto);

    @Mapping(target = "requestor", source = "itemRequest.requestor.id")
    ItemRequestResponse toItemRequestResponse(ItemRequest itemRequest);

    @Mapping(target = "requestor", source = "itemRequest.requestor.id")
    ItemRequestResponse.ItemRequestResponseItems toItemRequestResponseItems(ItemRequest itemRequest);
}
