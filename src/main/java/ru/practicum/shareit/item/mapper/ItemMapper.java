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
    ItemDtoResponse toItemDtoResponse(Item item);

    Item toItemDtoPatch(ItemDtoPatch item);

    ItemDtoResponse toItemDtoResponseOwner(Item item);

    @Mapper(componentModel = "spring")
    interface CommentMapper {

        Comment toComment(CommentDto commentDto);

        @Mapping(target = "authorName", source = "author")
        CommentDtoResponse toCommentDtoResponse(Comment comment);
    }
}
