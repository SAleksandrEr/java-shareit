package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepositoryJPA;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepositoryJpa;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    UserRepositoryJpa mockUserRepositoryJpa;

    @Mock
    ItemRequestRepositoryJPA itemRequestRepositoryJPA;

    @Mock
    ItemRepositoryJpa itemRepositoryJpa;

    @Mock
    ItemMapper itemMapper;

    @Mock
    BookingRepositoryJpa bookingRepositoryJpa;

    @Mock
    BookingMapper bookingMapper;
    @Mock
    ItemMapper.CommentMapper commentMapper;

    @Mock
    CommentRepositoryJpa commentRepositoryJpa;

    ItemDto itemDto = ItemDto.builder().name("test").description("test").available(true).requestId(1L).build();

    @Test
    void createItemException() {
        ItemService itemService = new ItemService(itemRepositoryJpa, itemMapper, mockUserRepositoryJpa, bookingRepositoryJpa, bookingMapper,
                commentMapper, commentRepositoryJpa, itemRequestRepositoryJPA);
        Mockito.when(mockUserRepositoryJpa.findById(Mockito.anyLong())).thenThrow(new DataNotFoundException("User not found"));
        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class, () -> itemService.createItem(1L, itemDto));

        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void createItemGetRequestIdExist() {
        ItemService itemService = new ItemService(itemRepositoryJpa, itemMapper, mockUserRepositoryJpa, bookingRepositoryJpa, bookingMapper,
                commentMapper, commentRepositoryJpa, itemRequestRepositoryJPA);
        User user = new User();
        user.setId(1L);
        user.setName("test");
        user.setEmail("test@test.ru");
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("test testovich");
        itemRequest.setCreated(LocalDateTime.of(2024,2, 9, 15,07));
        itemRequest.setRequestor(user);
        Item item = new Item();
        item.setId(1L);
        item.setName("test");
        item.setDescription("test");
        item.setAvailable(true);
        Mockito.when(mockUserRepositoryJpa.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepositoryJPA.findById(Mockito.anyLong())).thenReturn(Optional.of(itemRequest));
        Mockito.when(itemMapper.toItem(Mockito.any())).thenReturn(item);
        itemService.createItem(1L, itemDto);
        Assertions.assertEquals(itemDto.getRequestId(), item.getRequest().getId());
    }

    @Test
    void testUpdateItemException() {
        ItemService itemService = new ItemService(itemRepositoryJpa, itemMapper, mockUserRepositoryJpa, bookingRepositoryJpa, bookingMapper,
                commentMapper, commentRepositoryJpa, itemRequestRepositoryJPA);
        ItemDtoPatch itemDtoPatch = ItemDtoPatch.builder().id(1L).name("test").description("test").available(true).build();
        User user1 = new User();
        user1.setId(1L);
        user1.setName("test");
        user1.setEmail("test@test.ru");
        User user2 = new User();
        user2.setId(2L);
        user2.setName("test");
        user2.setEmail("test@test.ru");
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("test");
        item1.setDescription("test");
        item1.setAvailable(true);
        item1.setUser(user1);
        Item item2 = new Item();
        item2.setId(1L);
        item2.setName("test");
        item2.setDescription("test");
        item2.setAvailable(true);
        item2.setUser(user2);
        Mockito.when(mockUserRepositoryJpa.findById(Mockito.anyLong())).thenReturn(Optional.of(user1));
        Mockito.when(itemMapper.toItemDtoPatch(Mockito.any())).thenReturn(item1);
        Mockito.when(itemRepositoryJpa.findById(Mockito.any())).thenReturn(Optional.of(item2));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class, () -> itemService.updateItem(user1.getId(), itemDtoPatch));

        Assertions.assertEquals("Updating is not possible for the user - '" + user1.getId() +
                "' available only to the owner of the item", exception.getMessage());
    }

    @Test
    void testGetItemId() {
        ItemService itemService = new ItemService(itemRepositoryJpa, itemMapper, mockUserRepositoryJpa, bookingRepositoryJpa, bookingMapper,
                commentMapper, commentRepositoryJpa, itemRequestRepositoryJPA);
        User user = new User();
        user.setId(1L);
        user.setName("test");
        user.setEmail("test@test.ru");
        Item item = new Item();
        item.setId(1L);
        item.setName("test");
        item.setDescription("test");
        item.setAvailable(true);
        item.setUser(user);
        LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        LocalDateTime startDate1 = currentDate.minusDays(2);
        LocalDateTime endDate1 = currentDate.minusDays(1);
        LocalDateTime startDate2 = currentDate.plusDays(1);
        LocalDateTime endDate2 = currentDate.plusDays(2);
        ItemDtoResponse itemDtoResponse = ItemDtoResponse.builder().id(1L).name("test").description("test").available(true).requestId(1L).build();
        CommentDtoResponse commentDtoResponse = CommentDtoResponse.builder().id(1L).text("test").authorName("test1")
                .created(String.valueOf(LocalDateTime.of(2024,2, 9, 15,07))).build();
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("test");
        comment.setAuthor("test");
        comment.setItem(item);
        comment.setCreated(LocalDateTime.of(2024,2, 9, 15,07));
        List<Comment> commentList = new ArrayList<>();
        commentList.add(comment);
        List<Booking> bookings = new ArrayList<>();
        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setStart(startDate1);
        booking1.setEnd(endDate1);
        booking1.setItem(item);
        booking1.setBooker(user);
        booking1.setStatus(Status.APPROVED);
        bookings.add(booking1);
        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStart(startDate2);
        booking2.setEnd(endDate2);
        booking2.setItem(item);
        booking2.setBooker(user);
        booking2.setStatus(Status.APPROVED);
        bookings.add(booking2);
        BookingDtoResponse.BookingDtoResponseOwner bookingDtoResponseOwner1 = BookingDtoResponse.BookingDtoResponseOwner.builder()
                        .id(1L).bookerId(1L).build();
        BookingDtoResponse.BookingDtoResponseOwner bookingDtoResponseOwner2 = BookingDtoResponse.BookingDtoResponseOwner.builder()
                .id(2L).bookerId(1L).build();
        Mockito.when(mockUserRepositoryJpa.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRepositoryJpa.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(itemMapper.toItemDtoResponseOwner(Mockito.any())).thenReturn(itemDtoResponse);
        Mockito.when(commentRepositoryJpa.findByItemId(Mockito.any())).thenReturn(commentList);
        Mockito.when(commentMapper.toCommentDtoResponse(Mockito.any())).thenReturn(commentDtoResponse);
        Mockito.when(bookingRepositoryJpa.findByItemId(Mockito.any())).thenReturn(bookings);
        Mockito.when(bookingMapper.toBookingResponseOwner(Mockito.any())).thenAnswer(order -> {
            Booking booking = order.getArgument(0, Booking.class);
            if (booking.getId().equals(booking1.getId())) {
                return bookingDtoResponseOwner1;
            } else {
                return bookingDtoResponseOwner2;
            }
        });
        itemService.getItemId(1L, 1L);
        assertThat(itemDtoResponse.getLastBooking(), notNullValue());
        assertThat(itemDtoResponse.getNextBooking(), notNullValue());
        assertThat(itemDtoResponse.getLastBooking().getId(), equalTo(booking1.getId()));
        assertThat(itemDtoResponse.getNextBooking().getId(), equalTo(booking2.getId()));
    }


    @Test
    void testCreateCommentItemException() {
        ItemService itemService = new ItemService(itemRepositoryJpa, itemMapper, mockUserRepositoryJpa, bookingRepositoryJpa, bookingMapper,
                commentMapper, commentRepositoryJpa, itemRequestRepositoryJPA);
        CommentDto commentDto = CommentDto.builder().id(1L).text("test").itemId(1L).authorId(1L).build();
        List<Booking> bookings = new ArrayList<>();
        Mockito.when(bookingRepositoryJpa.findByItemIdAndBookerIdAndEndBefore(Mockito.anyLong(), Mockito.anyLong(), Mockito.any()))
                .thenReturn(bookings);
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class, () -> itemService.createCommentItem(1L, 1L, commentDto));

        Assertions.assertEquals("User not found or Booking not completed", exception.getMessage());
    }

}