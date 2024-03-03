package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingRepositoryJpa;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepositoryJpa;
import ru.practicum.shareit.item.storage.ItemRepositoryJpa;
import ru.practicum.shareit.request.storage.ItemRequestRepositoryJPA;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepositoryJpa;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
public class ItemService {

    private final ItemRepositoryJpa itemRepositoryJpa;

    private final ItemMapper itemMapper;

    private final UserRepositoryJpa userRepositoryJpa;

    private final BookingRepositoryJpa bookingRepositoryJpa;

    private final BookingMapper bookingMapper;

    private final ItemMapper.CommentMapper commentMapper;

    private final CommentRepositoryJpa commentRepositoryJpa;

    private final ItemRequestRepositoryJPA itemRequestRepositoryJPA;

    @Transactional
    public ItemDtoResponse createItem(Long userId, ItemDto itemDto) {
        Item item = itemMapper.toItem(itemDto);
        User user = userRepositoryJpa.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        if (itemDto.getRequestId() != null) {
            itemRequestRepositoryJPA.findById(itemDto.getRequestId()).ifPresent(item::setRequest);
        }
        item.setUser(user);
        return itemMapper.toItemDtoResponse(itemRepositoryJpa.save(item));
    }

    @Transactional
    public ItemDtoResponse updateItem(Long userId, ItemDto itemDtoPatch) {
        Item item = itemMapper.toItem(itemDtoPatch);
        User user = userRepositoryJpa.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        item.setUser(user);
        Item oldItem = itemRepositoryJpa.findById(item.getId())
                .orElseThrow(() -> new DataNotFoundException("Item not found"));
        if (item.getUser().getId().equals(oldItem.getUser().getId())) {
            if (item.getName() != null) {
                oldItem.setName(item.getName());
            }
            if (item.getDescription() != null) {
                oldItem.setDescription(item.getDescription());
            }
            if (item.getAvailable() != null) {
                oldItem.setAvailable(item.getAvailable());
            }
            log.info("Вещь обновлена " + oldItem);
            itemRepositoryJpa.updateItem(oldItem.getName(), oldItem.getDescription(),
                    oldItem.getAvailable(), oldItem.getId());
            return itemMapper.toItemDtoResponse(oldItem);
        } else {
            throw new ValidationException("Updating is not possible for the user - '" + userId +
                    "' available only to the owner of the item");
        }
    }

    public ItemDtoResponse getItemId(Long userId, Long id) {
        userRepositoryJpa.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        Item item = itemRepositoryJpa.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Item not found"));
        LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        ItemDtoResponse itemDtoResponse = itemMapper.toItemDtoResponseOwner(item);
        itemDtoResponse.setComments(commentRepositoryJpa.findByItemId(item.getId()).stream()
                .map(commentMapper::toCommentDtoResponse).collect(Collectors.toList()));
        if (Objects.equals(item.getUser().getId(), userId)) {
            List<Booking> bookings = bookingRepositoryJpa.findByItemId(item.getId());
            if (bookings.size() > 0) {
                for (Booking order : bookings) {
                    if (order.getStart().isBefore(currentDate) && !order.getStatus().equals(Status.REJECTED)) {
                        BookingDtoResponse.BookingDtoResponseOwner bookingDtoResponse = bookingMapper.toBookingResponseOwner(order);
                        itemDtoResponse.setLastBooking(bookingDtoResponse);
                    }
                    if (order.getStart().isAfter(currentDate) && !order.getStatus().equals(Status.REJECTED)) {
                        BookingDtoResponse.BookingDtoResponseOwner bookingDtoResponse = bookingMapper.toBookingResponseOwner(order);
                        itemDtoResponse.setNextBooking(bookingDtoResponse);
                    }
                }
            }
        }
            log.info("Получена вещь с id " + id);
            return itemDtoResponse;
    }

    public List<ItemDtoResponse> findItemsByUserId(Long userid, Pageable page) {
        userRepositoryJpa.findById(userid)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        List<Item> items = itemRepositoryJpa.findItemsByUserId(userid, page).getContent();
        log.info("Получены вещи пользователя с id " + userid);
        LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        return items.stream()
                .map(item -> {
                    ItemDtoResponse itemDtoResponse = itemMapper.toItemDtoResponseOwner(item);
                    itemDtoResponse.setComments(commentRepositoryJpa.findByItemId(item.getId()).stream()
                            .map(commentMapper::toCommentDtoResponse).collect(Collectors.toList()));
                    List<Booking> bookings = bookingRepositoryJpa.findByItemId(item.getId());
                        if (bookings.size() > 0) {
                            for (Booking book : bookings) {
                                if (book.getStart().isBefore(currentDate) && !book.getStatus().equals(Status.REJECTED)) {
                                    BookingDtoResponse.BookingDtoResponseOwner bookingDtoResponse = bookingMapper.toBookingResponseOwner(book);
                                    itemDtoResponse.setLastBooking(bookingDtoResponse);
                                }
                                if (book.getStart().isAfter(currentDate) && !book.getStatus().equals(Status.REJECTED)) {
                                    BookingDtoResponse.BookingDtoResponseOwner bookingDtoResponse = bookingMapper.toBookingResponseOwner(book);
                                    itemDtoResponse.setNextBooking(bookingDtoResponse);
                                }
                            }
                        }
                        return itemDtoResponse;
                    }).collect(Collectors.toList());
    }

    public List<ItemDtoResponse> searchNameItemsAndDescription(String query, Pageable page) {
        if (query.isEmpty()) {
            return new ArrayList<>();
        }
        List<Item> items = itemRepositoryJpa.findByNameAndDescription(query.toLowerCase(), page).getContent();
        log.info("Найдены вещи по запросу - " + query);
        return items.stream()
                .map(itemMapper::toItemDtoResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentDtoResponse createCommentItem(Long userId, Long itemId, CommentDto commentDto) {
        validate(userId, itemId);
        Comment comment = commentMapper.toComment(commentDto);
        User user = userRepositoryJpa.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        Item item = itemRepositoryJpa.findById(itemId)
                .orElseThrow(() -> new DataNotFoundException("Item not found"));
        comment.setAuthor(user.getName());
        comment.setItem(item);
        return commentMapper.toCommentDtoResponse(commentRepositoryJpa.save(comment));
    }

    private void validate(Long userId, Long itemId) {
        LocalDateTime currentDate = LocalDateTime.now();
        if (bookingRepositoryJpa.findByItemIdAndBookerIdAndEndBefore(itemId, userId, currentDate).size() == 0) {
            throw new ValidationException("User not found or Booking not completed");
        }
    }
}
