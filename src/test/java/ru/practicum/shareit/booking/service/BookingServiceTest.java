package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingRepositoryJpa;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepositoryJpa;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepositoryJpa;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    BookingMapper bookingMapper;
    @Mock
    BookingRepositoryJpa bookingRepositoryJpa;
    @Mock
    UserRepositoryJpa userRepositoryJpa;
    @Mock
    ItemRepositoryJpa itemRepositoryJpa;

    LocalDateTime currentDate = LocalDateTime.now().withNano(0);
    LocalDateTime startDate1 = currentDate.plusMinutes(2);
    LocalDateTime endDate1 = currentDate.plusMinutes(4);
    BookingDto bookingDto = BookingDto.builder()
            .start(String.valueOf(startDate1)).end(String.valueOf(endDate1)).itemId(1L).bookerId(2L).build();

    @Test
    void testsCreateBookingPositive() {
        BookingService bookingService = new BookingService(bookingMapper, bookingRepositoryJpa, userRepositoryJpa, itemRepositoryJpa);
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
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(startDate1);
        booking.setEnd(endDate1);
        Mockito.when(userRepositoryJpa.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRepositoryJpa.findById(anyLong())).thenReturn(Optional.of(item));
        Mockito.when(bookingMapper.toBooking(any())).thenReturn(booking);
        Mockito.when(itemRepositoryJpa.findByIdAndAvailableTrue(anyLong())).thenReturn(item);
        bookingService.createBooking(2L, bookingDto);
        Assertions.assertEquals(booking.getItem().getId(), item.getId());
    }

    @Test
    void testsCreateBookingException() {
        BookingService bookingService = new BookingService(bookingMapper, bookingRepositoryJpa, userRepositoryJpa, itemRepositoryJpa);
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
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(startDate1);
        booking.setEnd(endDate1);
        Mockito.when(userRepositoryJpa.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRepositoryJpa.findById(anyLong())).thenReturn(Optional.of(item));
        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class, () -> bookingService.createBooking(1L, bookingDto));

        Assertions.assertEquals("The owner cannot accept the Booking " + item.getId(), exception.getMessage());
    }

    @Test
    void testsCreateBookingExceptionLastTime() {
        BookingService bookingService = new BookingService(bookingMapper, bookingRepositoryJpa, userRepositoryJpa, itemRepositoryJpa);
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
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(endDate1);
        booking.setEnd(startDate1);
        bookingDto.setStart(String.valueOf(endDate1));
        bookingDto.setEnd(String.valueOf(startDate1));
        Mockito.when(userRepositoryJpa.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRepositoryJpa.findById(anyLong())).thenReturn(Optional.of(item));
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class, () -> bookingService.createBooking(2L, bookingDto));

        Assertions.assertEquals("Invalid data " + booking.getStart() + " " + booking.getEnd(), exception.getMessage());
    }

    @Test
    void testUpdateStatusBookingPositive() {
        BookingService bookingService = new BookingService(bookingMapper, bookingRepositoryJpa, userRepositoryJpa, itemRepositoryJpa);
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
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(startDate1);
        booking.setEnd(endDate1);
        booking.setStatus(Status.WAITING);
        booking.setBooker(user);
        Mockito.when(userRepositoryJpa.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(bookingRepositoryJpa.findByBookingIdAndOwner(anyLong(), anyLong())).thenReturn(Optional.of(booking));
        Mockito.when(bookingRepositoryJpa.updateBooking(any(), anyLong())).thenReturn(1);
        bookingService.updateStatusBooking(user.getId(), booking.getId(), true);
        Assertions.assertEquals(booking.getStatus(), Status.APPROVED);
    }

    @Test
    void testUpdateStatusBookingException() {
        BookingService bookingService = new BookingService(bookingMapper, bookingRepositoryJpa, userRepositoryJpa, itemRepositoryJpa);
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
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(startDate1);
        booking.setEnd(endDate1);
        booking.setStatus(Status.WAITING);
        booking.setBooker(user);
        Mockito.when(userRepositoryJpa.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(bookingRepositoryJpa.findByBookingIdAndOwner(anyLong(), anyLong())).thenReturn(Optional.of(booking));
        Mockito.when(bookingRepositoryJpa.updateBooking(any(), anyLong())).thenReturn(0);
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class, () -> bookingService.updateStatusBooking(user.getId(), booking.getId(), true));

        Assertions.assertEquals("Booking status is APPROVED ", exception.getMessage());
    }

    @Test
    void testUpdateStatusBookingExceptionNotOwner() {
        BookingService bookingService = new BookingService(bookingMapper, bookingRepositoryJpa, userRepositoryJpa, itemRepositoryJpa);
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
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(startDate1);
        booking.setEnd(endDate1);
        booking.setStatus(Status.WAITING);
        booking.setBooker(user);
        Mockito.when(userRepositoryJpa.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(bookingRepositoryJpa.findByBookingIdAndOwner(anyLong(), anyLong())).thenThrow(new DataNotFoundException("User is not owner"));
        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class, () -> bookingService.updateStatusBooking(user.getId(), booking.getId(), true));

        Assertions.assertEquals("User is not owner", exception.getMessage());
    }

    @Test
    void testUpdateStatusBookingNegative() {
        BookingService bookingService = new BookingService(bookingMapper, bookingRepositoryJpa, userRepositoryJpa, itemRepositoryJpa);
        User user = new User();
        user.setId(1L);
        user.setName("test");
        user.setEmail("test@test.ru");
        Item item = new Item();
        item.setId(1L);
        item.setName("test");
        item.setDescription("test");
        item.setAvailable(false);
        item.setUser(user);
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(startDate1);
        booking.setEnd(endDate1);
        booking.setStatus(Status.WAITING);
        booking.setBooker(user);
        Mockito.when(userRepositoryJpa.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(bookingRepositoryJpa.findByBookingIdAndOwner(anyLong(), anyLong())).thenReturn(Optional.of(booking));
        Mockito.when(bookingRepositoryJpa.updateBooking(any(), anyLong())).thenReturn(1);
        bookingService.updateStatusBooking(user.getId(), booking.getId(), false);
        Assertions.assertEquals(booking.getStatus(), Status.REJECTED);
    }

    @Test
    void testsCreateBookingExceptionNull() {
        BookingService bookingService = new BookingService(bookingMapper, bookingRepositoryJpa, userRepositoryJpa, itemRepositoryJpa);
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
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(startDate1);
        booking.setEnd(endDate1);
        Mockito.when(userRepositoryJpa.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRepositoryJpa.findById(anyLong())).thenReturn(Optional.of(item));
        Mockito.when(itemRepositoryJpa.findByIdAndAvailableTrue(anyLong())).thenReturn(null);
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class, () ->  bookingService.createBooking(2L, bookingDto));

        Assertions.assertEquals("Invalid data " + booking.getStart() + " " + booking.getEnd(), exception.getMessage());
    }
}