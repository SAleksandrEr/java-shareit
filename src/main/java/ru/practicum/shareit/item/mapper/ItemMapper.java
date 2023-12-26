package ru.practicum.shareit.item.mapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoPatch;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "owner", source = "userId")
    Item toItem(Long userId, ItemDto item);

    ItemDtoResponse toItemDtoResponse(Item item);

    @Mapping(target = "owner", source = "userId")
    Item toItemDtoPatch(Long userId, ItemDtoPatch item);
}
