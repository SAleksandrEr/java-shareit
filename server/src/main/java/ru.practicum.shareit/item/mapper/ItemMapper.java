package ru.practicum.shareit.item.mapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    Item toItem(ItemDto item);

    @Mapping(target = "lastBooking", ignore = true)
    @Mapping(target = "nextBooking", ignore = true)
    @Mapping(target = "requestId", source = "item.request.id")
    ItemDtoResponse toItemDtoResponse(Item item);

    @Mapping(target = "requestId", source = "item.request.id")
    ItemDtoResponse toItemDtoResponseOwner(Item item);

    @Mapping(target = "requestId", source = "item.request.id")
    ItemDtoResponse.ItemDtoResponseRequest toItemDtoResponseRequest(Item item);

    @Mapper(componentModel = "spring")
    interface CommentMapper {

        Comment toComment(CommentDto commentDto);

        @Mapping(target = "authorName", source = "author")
        CommentDtoResponse toCommentDtoResponse(Comment comment);
    }
}
