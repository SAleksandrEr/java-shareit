package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.GetBookingParam;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.service.UserService;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIntTest {
    private final UserService userService;
    private final BookingService bookingService;
    private final ItemService itemService;

    @Test
    void testGetBookingByCurrentUser() {
        UserDtoRequest userDtoBooker = UserDtoRequest.builder().name("testBooker").email("testBooker@test.ru").build();
        UserDtoRequest userDtoOwner = UserDtoRequest.builder().name("testOwner").email("testOwner@test.ru").build();
        UserResponse userResponseOwner = userService.createUser(userDtoOwner);
        UserResponse userResponseBooker = userService.createUser(userDtoBooker);
        ItemDto itemDto = ItemDto.builder().name("test1").description("test1").available(true).build();
        ItemDtoResponse itemDtoResponse = itemService.createItem(userResponseOwner.getId(), itemDto);
        LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        LocalDateTime startDate1 = currentDate.plusSeconds(1);
        LocalDateTime endDate1 = currentDate.plusSeconds(2);
        BookingDto bookingDto = BookingDto.builder().start(String.valueOf(startDate1)).end(String.valueOf(endDate1))
                .itemId(itemDtoResponse.getId()).bookerId(userResponseBooker.getId()).build();
        BookingDtoResponse bookingDtoResponse = bookingService.createBooking(userResponseBooker.getId(),bookingDto);
        List<BookingDtoResponse> bookingDtoResponseList = bookingService.getBookingByCurrentUser(userResponseBooker.getId(), State.ALL, GetBookingParam.pageRequest(1,10));
        assertThat(bookingDtoResponseList.size(), notNullValue());
        assertThat(bookingDtoResponseList.get(0).getId(), equalTo(bookingDtoResponse.getId()));
        assertThat(bookingDtoResponseList.get(0).getStart(), equalTo(bookingDtoResponse.getStart()));
        assertThat(bookingDtoResponseList.get(0).getEnd(), equalTo(bookingDtoResponse.getEnd()));
        assertThat(bookingDtoResponseList.get(0).getStatus(), equalTo(bookingDtoResponse.getStatus()));
        assertThat(bookingDtoResponseList.get(0).getBooker().getId(), equalTo(bookingDtoResponse.getBooker().getId()));
        assertThat(bookingDtoResponseList.get(0).getItem().getId(), equalTo(bookingDtoResponse.getItem().getId()));
        assertThat(bookingDtoResponseList.get(0).getItem().getName(), equalTo(bookingDtoResponse.getItem().getName()));
    }

    @Test
    void testGetBookingByCurrentUserWaitingStatus() {
        UserDtoRequest userDtoBooker = UserDtoRequest.builder().name("testBooker").email("testBooker@test.ru").build();
        UserDtoRequest userDtoOwner = UserDtoRequest.builder().name("testOwner").email("testOwner@test.ru").build();
        UserResponse userResponseOwner = userService.createUser(userDtoOwner);
        UserResponse userResponseBooker = userService.createUser(userDtoBooker);
        ItemDto itemDto = ItemDto.builder().name("test1").description("test1").available(true).build();
        ItemDtoResponse itemDtoResponse = itemService.createItem(userResponseOwner.getId(), itemDto);
        LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        LocalDateTime startDate1 = currentDate.plusSeconds(1);
        LocalDateTime endDate1 = currentDate.plusSeconds(2);
        BookingDto bookingDto = BookingDto.builder().start(String.valueOf(startDate1)).end(String.valueOf(endDate1))
                .itemId(itemDtoResponse.getId()).bookerId(userResponseBooker.getId()).build();
        BookingDtoResponse bookingDtoResponse = bookingService.createBooking(userResponseBooker.getId(),bookingDto);
        List<BookingDtoResponse> bookingDtoResponseList = bookingService.getBookingByCurrentUser(userResponseBooker.getId(), State.WAITING, GetBookingParam.pageRequest(1,10));
        assertThat(bookingDtoResponseList.size(), notNullValue());
        assertThat(bookingDtoResponseList.get(0).getId(), equalTo(bookingDtoResponse.getId()));
        assertThat(bookingDtoResponseList.get(0).getStart(), equalTo(bookingDtoResponse.getStart()));
        assertThat(bookingDtoResponseList.get(0).getEnd(), equalTo(bookingDtoResponse.getEnd()));
        assertThat(bookingDtoResponseList.get(0).getStatus(), equalTo(bookingDtoResponse.getStatus()));
        assertThat(bookingDtoResponseList.get(0).getBooker().getId(), equalTo(bookingDtoResponse.getBooker().getId()));
        assertThat(bookingDtoResponseList.get(0).getItem().getId(), equalTo(bookingDtoResponse.getItem().getId()));
        assertThat(bookingDtoResponseList.get(0).getItem().getName(), equalTo(bookingDtoResponse.getItem().getName()));
    }

    @Test
    void testGetBookingByCurrentUserCurrentStatus() throws InterruptedException {
        UserDtoRequest userDtoBooker = UserDtoRequest.builder().name("testBooker").email("testBooker@test.ru").build();
        UserDtoRequest userDtoOwner = UserDtoRequest.builder().name("testOwner").email("testOwner@test.ru").build();
        UserResponse userResponseOwner = userService.createUser(userDtoOwner);
        UserResponse userResponseBooker = userService.createUser(userDtoBooker);
        ItemDto itemDto = ItemDto.builder().name("test1").description("test1").available(true).build();
        ItemDtoResponse itemDtoResponse = itemService.createItem(userResponseOwner.getId(), itemDto);
        LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        LocalDateTime startDate1 = currentDate.plusSeconds(1);
        LocalDateTime endDate1 = currentDate.plusHours(1);
        BookingDto bookingDto = BookingDto.builder().start(String.valueOf(startDate1)).end(String.valueOf(endDate1))
                .itemId(itemDtoResponse.getId()).bookerId(userResponseBooker.getId()).build();
        BookingDtoResponse bookingDtoResponse = bookingService.createBooking(userResponseBooker.getId(),bookingDto);
        Thread.sleep(20_00);
        List<BookingDtoResponse> bookingDtoResponseList = bookingService.getBookingByCurrentUser(userResponseBooker.getId(), State.CURRENT, GetBookingParam.pageRequest(1,10));
        assertThat(bookingDtoResponseList.size(), notNullValue());
        assertThat(bookingDtoResponseList.get(0).getId(), equalTo(bookingDtoResponse.getId()));
        assertThat(bookingDtoResponseList.get(0).getStart(), equalTo(bookingDtoResponse.getStart()));
        assertThat(bookingDtoResponseList.get(0).getEnd(), equalTo(bookingDtoResponse.getEnd()));
        assertThat(bookingDtoResponseList.get(0).getStatus(), equalTo(bookingDtoResponse.getStatus()));
        assertThat(bookingDtoResponseList.get(0).getBooker().getId(), equalTo(bookingDtoResponse.getBooker().getId()));
        assertThat(bookingDtoResponseList.get(0).getItem().getId(), equalTo(bookingDtoResponse.getItem().getId()));
        assertThat(bookingDtoResponseList.get(0).getItem().getName(), equalTo(bookingDtoResponse.getItem().getName()));
    }

    @Test
    void testGetBookingByCurrentUserPastStatus() throws InterruptedException {
        UserDtoRequest userDtoBooker = UserDtoRequest.builder().name("testBooker").email("testBooker@test.ru").build();
        UserDtoRequest userDtoOwner = UserDtoRequest.builder().name("testOwner").email("testOwner@test.ru").build();
        UserResponse userResponseOwner = userService.createUser(userDtoOwner);
        UserResponse userResponseBooker = userService.createUser(userDtoBooker);
        ItemDto itemDto = ItemDto.builder().name("test1").description("test1").available(true).build();
        ItemDtoResponse itemDtoResponse = itemService.createItem(userResponseOwner.getId(), itemDto);
        LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        LocalDateTime startDate1 = currentDate.plusSeconds(1);
        LocalDateTime endDate1 = currentDate.plusSeconds(2);
        BookingDto bookingDto = BookingDto.builder().start(String.valueOf(startDate1)).end(String.valueOf(endDate1))
                .itemId(itemDtoResponse.getId()).bookerId(userResponseBooker.getId()).build();
        BookingDtoResponse bookingDtoResponse = bookingService.createBooking(userResponseBooker.getId(),bookingDto);
        Thread.sleep(30_00);
        List<BookingDtoResponse> bookingDtoResponseList = bookingService.getBookingByCurrentUser(userResponseBooker.getId(), State.PAST, GetBookingParam.pageRequest(1,10));
        assertThat(bookingDtoResponseList.size(), notNullValue());
        assertThat(bookingDtoResponseList.get(0).getId(), equalTo(bookingDtoResponse.getId()));
        assertThat(bookingDtoResponseList.get(0).getStart(), equalTo(bookingDtoResponse.getStart()));
        assertThat(bookingDtoResponseList.get(0).getEnd(), equalTo(bookingDtoResponse.getEnd()));
        assertThat(bookingDtoResponseList.get(0).getStatus(), equalTo(bookingDtoResponse.getStatus()));
        assertThat(bookingDtoResponseList.get(0).getBooker().getId(), equalTo(bookingDtoResponse.getBooker().getId()));
        assertThat(bookingDtoResponseList.get(0).getItem().getId(), equalTo(bookingDtoResponse.getItem().getId()));
        assertThat(bookingDtoResponseList.get(0).getItem().getName(), equalTo(bookingDtoResponse.getItem().getName()));
    }

    @Test
    void testGetBookingByCurrentUserFutureStatus() {
        UserDtoRequest userDtoBooker = UserDtoRequest.builder().name("testBooker").email("testBooker@test.ru").build();
        UserDtoRequest userDtoOwner = UserDtoRequest.builder().name("testOwner").email("testOwner@test.ru").build();
        UserResponse userResponseOwner = userService.createUser(userDtoOwner);
        UserResponse userResponseBooker = userService.createUser(userDtoBooker);
        ItemDto itemDto = ItemDto.builder().name("test1").description("test1").available(true).build();
        ItemDtoResponse itemDtoResponse = itemService.createItem(userResponseOwner.getId(), itemDto);
        LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        LocalDateTime startDate1 = currentDate.plusHours(1);
        LocalDateTime endDate1 = currentDate.plusHours(2);
        BookingDto bookingDto = BookingDto.builder().start(String.valueOf(startDate1)).end(String.valueOf(endDate1))
                .itemId(itemDtoResponse.getId()).bookerId(userResponseBooker.getId()).build();
        BookingDtoResponse bookingDtoResponse = bookingService.createBooking(userResponseBooker.getId(),bookingDto);
        List<BookingDtoResponse> bookingDtoResponseList = bookingService.getBookingByCurrentUser(userResponseBooker.getId(), State.FUTURE, GetBookingParam.pageRequest(1,10));
        assertThat(bookingDtoResponseList.size(), notNullValue());
        assertThat(bookingDtoResponseList.get(0).getId(), equalTo(bookingDtoResponse.getId()));
        assertThat(bookingDtoResponseList.get(0).getStart(), equalTo(bookingDtoResponse.getStart()));
        assertThat(bookingDtoResponseList.get(0).getEnd(), equalTo(bookingDtoResponse.getEnd()));
        assertThat(bookingDtoResponseList.get(0).getStatus(), equalTo(bookingDtoResponse.getStatus()));
        assertThat(bookingDtoResponseList.get(0).getBooker().getId(), equalTo(bookingDtoResponse.getBooker().getId()));
        assertThat(bookingDtoResponseList.get(0).getItem().getId(), equalTo(bookingDtoResponse.getItem().getId()));
        assertThat(bookingDtoResponseList.get(0).getItem().getName(), equalTo(bookingDtoResponse.getItem().getName()));
    }

    @Test
    void testGetBookingByCurrentUserRejected() {
        UserDtoRequest userDtoBooker = UserDtoRequest.builder().name("testBooker").email("testBooker@test.ru").build();
        UserDtoRequest userDtoOwner = UserDtoRequest.builder().name("testOwner").email("testOwner@test.ru").build();
        UserResponse userResponseOwner = userService.createUser(userDtoOwner);
        UserResponse userResponseBooker = userService.createUser(userDtoBooker);
        ItemDto itemDto = ItemDto.builder().name("test1").description("test1").available(true).build();
        ItemDtoResponse itemDtoResponse = itemService.createItem(userResponseOwner.getId(), itemDto);
        LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        LocalDateTime startDate1 = currentDate.plusHours(1);
        LocalDateTime endDate1 = currentDate.plusHours(2);
        BookingDto bookingDto = BookingDto.builder().start(String.valueOf(startDate1)).end(String.valueOf(endDate1))
                .itemId(itemDtoResponse.getId()).bookerId(userResponseBooker.getId()).build();
        BookingDtoResponse bookingDtoResponse = bookingService.createBooking(userResponseBooker.getId(),bookingDto);
        bookingDtoResponse = bookingService.updateStatusBooking(userResponseOwner.getId(), bookingDtoResponse.getId(), false);
        List<BookingDtoResponse> bookingDtoResponseList = bookingService.getBookingByCurrentUser(userResponseBooker.getId(), State.REJECTED, GetBookingParam.pageRequest(1,10));
        assertThat(bookingDtoResponseList.size(), notNullValue());
        assertThat(bookingDtoResponseList.get(0).getId(), equalTo(bookingDtoResponse.getId()));
        assertThat(bookingDtoResponseList.get(0).getStart(), equalTo(bookingDtoResponse.getStart()));
        assertThat(bookingDtoResponseList.get(0).getEnd(), equalTo(bookingDtoResponse.getEnd()));
        assertThat(bookingDtoResponseList.get(0).getStatus(), equalTo(bookingDtoResponse.getStatus()));
        assertThat(bookingDtoResponseList.get(0).getBooker().getId(), equalTo(bookingDtoResponse.getBooker().getId()));
        assertThat(bookingDtoResponseList.get(0).getItem().getId(), equalTo(bookingDtoResponse.getItem().getId()));
        assertThat(bookingDtoResponseList.get(0).getItem().getName(), equalTo(bookingDtoResponse.getItem().getName()));
    }

    @Test
    void testGetBookingByCurrentUserUnSupported() {
        UserDtoRequest userDtoBooker = UserDtoRequest.builder().name("testBooker").email("testBooker@test.ru").build();
        UserDtoRequest userDtoOwner = UserDtoRequest.builder().name("testOwner").email("testOwner@test.ru").build();
        UserResponse userResponseOwner = userService.createUser(userDtoOwner);
        UserResponse userResponseBooker = userService.createUser(userDtoBooker);
        ItemDto itemDto = ItemDto.builder().name("test1").description("test1").available(true).build();
        ItemDtoResponse itemDtoResponse = itemService.createItem(userResponseOwner.getId(), itemDto);
        LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        LocalDateTime startDate1 = currentDate.plusHours(1);
        LocalDateTime endDate1 = currentDate.plusHours(2);
        BookingDto bookingDto = BookingDto.builder().start(String.valueOf(startDate1)).end(String.valueOf(endDate1))
                .itemId(itemDtoResponse.getId()).bookerId(userResponseBooker.getId()).build();
        BookingDtoResponse bookingDtoResponse = bookingService.createBooking(userResponseBooker.getId(),bookingDto);
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class, () -> bookingService.getBookingByCurrentUser(userResponseBooker.getId(), State.UNSUPPORTED_STATUS, GetBookingParam.pageRequest(1,10)));

        Assertions.assertEquals("Unknown state: " + State.UNSUPPORTED_STATUS, exception.getMessage());
    }

    @Test
    void testGetBookingByOwner() {
        UserDtoRequest userDtoBooker = UserDtoRequest.builder().name("testBooker").email("testBooker@test.ru").build();
        UserDtoRequest userDtoOwner = UserDtoRequest.builder().name("testOwner").email("testOwner@test.ru").build();
        UserResponse userResponseOwner = userService.createUser(userDtoOwner);
        UserResponse userResponseBooker = userService.createUser(userDtoBooker);
        ItemDto itemDto = ItemDto.builder().name("test1").description("test1").available(true).build();
        ItemDtoResponse itemDtoResponse = itemService.createItem(userResponseOwner.getId(), itemDto);
        LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        LocalDateTime startDate1 = currentDate.plusHours(1);
        LocalDateTime endDate1 = currentDate.plusHours(2);
        BookingDto bookingDto = BookingDto.builder().start(String.valueOf(startDate1)).end(String.valueOf(endDate1))
                .itemId(itemDtoResponse.getId()).bookerId(userResponseBooker.getId()).build();
        BookingDtoResponse bookingDtoResponse = bookingService.createBooking(userResponseBooker.getId(),bookingDto);
        List<BookingDtoResponse> bookingDtoResponseList = bookingService.getBookingByOwner(userResponseOwner.getId(), State.ALL, GetBookingParam.pageRequest(1,10));
        assertThat(bookingDtoResponseList.size(), notNullValue());
        assertThat(bookingDtoResponseList.get(0).getId(), equalTo(bookingDtoResponse.getId()));
        assertThat(bookingDtoResponseList.get(0).getStart(), equalTo(bookingDtoResponse.getStart()));
        assertThat(bookingDtoResponseList.get(0).getEnd(), equalTo(bookingDtoResponse.getEnd()));
        assertThat(bookingDtoResponseList.get(0).getStatus(), equalTo(bookingDtoResponse.getStatus()));
        assertThat(bookingDtoResponseList.get(0).getBooker().getId(), equalTo(bookingDtoResponse.getBooker().getId()));
        assertThat(bookingDtoResponseList.get(0).getItem().getId(), equalTo(bookingDtoResponse.getItem().getId()));
        assertThat(bookingDtoResponseList.get(0).getItem().getName(), equalTo(bookingDtoResponse.getItem().getName()));
    }

    @Test
    void testGetBookingByOwnerWaitingStatus() {
        UserDtoRequest userDtoBooker = UserDtoRequest.builder().name("testBooker").email("testBooker@test.ru").build();
        UserDtoRequest userDtoOwner = UserDtoRequest.builder().name("testOwner").email("testOwner@test.ru").build();
        UserResponse userResponseOwner = userService.createUser(userDtoOwner);
        UserResponse userResponseBooker = userService.createUser(userDtoBooker);
        ItemDto itemDto = ItemDto.builder().name("test1").description("test1").available(true).build();
        ItemDtoResponse itemDtoResponse = itemService.createItem(userResponseOwner.getId(), itemDto);
        LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        LocalDateTime startDate1 = currentDate.plusHours(1);
        LocalDateTime endDate1 = currentDate.plusHours(2);
        BookingDto bookingDto = BookingDto.builder().start(String.valueOf(startDate1)).end(String.valueOf(endDate1))
                .itemId(itemDtoResponse.getId()).bookerId(userResponseBooker.getId()).build();
        BookingDtoResponse bookingDtoResponse = bookingService.createBooking(userResponseBooker.getId(),bookingDto);
        List<BookingDtoResponse> bookingDtoResponseList = bookingService.getBookingByOwner(userResponseOwner.getId(), State.WAITING, GetBookingParam.pageRequest(1,10));
        assertThat(bookingDtoResponseList.size(), notNullValue());
        assertThat(bookingDtoResponseList.get(0).getId(), equalTo(bookingDtoResponse.getId()));
        assertThat(bookingDtoResponseList.get(0).getStart(), equalTo(bookingDtoResponse.getStart()));
        assertThat(bookingDtoResponseList.get(0).getEnd(), equalTo(bookingDtoResponse.getEnd()));
        assertThat(bookingDtoResponseList.get(0).getStatus(), equalTo(bookingDtoResponse.getStatus()));
        assertThat(bookingDtoResponseList.get(0).getBooker().getId(), equalTo(bookingDtoResponse.getBooker().getId()));
        assertThat(bookingDtoResponseList.get(0).getItem().getId(), equalTo(bookingDtoResponse.getItem().getId()));
        assertThat(bookingDtoResponseList.get(0).getItem().getName(), equalTo(bookingDtoResponse.getItem().getName()));
    }

    @Test
    void testGetBookingByOwnerCurrentStatus() throws InterruptedException {
        UserDtoRequest userDtoBooker = UserDtoRequest.builder().name("testBooker").email("testBooker@test.ru").build();
        UserDtoRequest userDtoOwner = UserDtoRequest.builder().name("testOwner").email("testOwner@test.ru").build();
        UserResponse userResponseOwner = userService.createUser(userDtoOwner);
        UserResponse userResponseBooker = userService.createUser(userDtoBooker);
        ItemDto itemDto = ItemDto.builder().name("test1").description("test1").available(true).build();
        ItemDtoResponse itemDtoResponse = itemService.createItem(userResponseOwner.getId(), itemDto);
        LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        LocalDateTime startDate1 = currentDate.plusSeconds(1);
        LocalDateTime endDate1 = currentDate.plusHours(1);
        BookingDto bookingDto = BookingDto.builder().start(String.valueOf(startDate1)).end(String.valueOf(endDate1))
                .itemId(itemDtoResponse.getId()).bookerId(userResponseBooker.getId()).build();
        BookingDtoResponse bookingDtoResponse = bookingService.createBooking(userResponseBooker.getId(),bookingDto);
        Thread.sleep(20_00);
        List<BookingDtoResponse> bookingDtoResponseList = bookingService.getBookingByOwner(userResponseOwner.getId(), State.CURRENT, GetBookingParam.pageRequest(1,10));
        assertThat(bookingDtoResponseList.size(), notNullValue());
        assertThat(bookingDtoResponseList.get(0).getId(), equalTo(bookingDtoResponse.getId()));
        assertThat(bookingDtoResponseList.get(0).getStart(), equalTo(bookingDtoResponse.getStart()));
        assertThat(bookingDtoResponseList.get(0).getEnd(), equalTo(bookingDtoResponse.getEnd()));
        assertThat(bookingDtoResponseList.get(0).getStatus(), equalTo(bookingDtoResponse.getStatus()));
        assertThat(bookingDtoResponseList.get(0).getBooker().getId(), equalTo(bookingDtoResponse.getBooker().getId()));
        assertThat(bookingDtoResponseList.get(0).getItem().getId(), equalTo(bookingDtoResponse.getItem().getId()));
        assertThat(bookingDtoResponseList.get(0).getItem().getName(), equalTo(bookingDtoResponse.getItem().getName()));
    }

    @Test
    void testGetBookingByOwnerPastStatus() throws InterruptedException {
        UserDtoRequest userDtoBooker = UserDtoRequest.builder().name("testBooker").email("testBooker@test.ru").build();
        UserDtoRequest userDtoOwner = UserDtoRequest.builder().name("testOwner").email("testOwner@test.ru").build();
        UserResponse userResponseOwner = userService.createUser(userDtoOwner);
        UserResponse userResponseBooker = userService.createUser(userDtoBooker);
        ItemDto itemDto = ItemDto.builder().name("test1").description("test1").available(true).build();
        ItemDtoResponse itemDtoResponse = itemService.createItem(userResponseOwner.getId(), itemDto);
        LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        LocalDateTime startDate1 = currentDate.plusSeconds(1);
        LocalDateTime endDate1 = currentDate.plusSeconds(2);
        BookingDto bookingDto = BookingDto.builder().start(String.valueOf(startDate1)).end(String.valueOf(endDate1))
                .itemId(itemDtoResponse.getId()).bookerId(userResponseBooker.getId()).build();
        BookingDtoResponse bookingDtoResponse = bookingService.createBooking(userResponseBooker.getId(),bookingDto);
        Thread.sleep(30_00);
        List<BookingDtoResponse> bookingDtoResponseList = bookingService.getBookingByOwner(userResponseOwner.getId(), State.PAST, GetBookingParam.pageRequest(1,10));
        assertThat(bookingDtoResponseList.size(), notNullValue());
        assertThat(bookingDtoResponseList.get(0).getId(), equalTo(bookingDtoResponse.getId()));
        assertThat(bookingDtoResponseList.get(0).getStart(), equalTo(bookingDtoResponse.getStart()));
        assertThat(bookingDtoResponseList.get(0).getEnd(), equalTo(bookingDtoResponse.getEnd()));
        assertThat(bookingDtoResponseList.get(0).getStatus(), equalTo(bookingDtoResponse.getStatus()));
        assertThat(bookingDtoResponseList.get(0).getBooker().getId(), equalTo(bookingDtoResponse.getBooker().getId()));
        assertThat(bookingDtoResponseList.get(0).getItem().getId(), equalTo(bookingDtoResponse.getItem().getId()));
        assertThat(bookingDtoResponseList.get(0).getItem().getName(), equalTo(bookingDtoResponse.getItem().getName()));
    }

    @Test
    void testGetBookingByOwnerFutureStatus() {
        UserDtoRequest userDtoBooker = UserDtoRequest.builder().name("testBooker").email("testBooker@test.ru").build();
        UserDtoRequest userDtoOwner = UserDtoRequest.builder().name("testOwner").email("testOwner@test.ru").build();
        UserResponse userResponseOwner = userService.createUser(userDtoOwner);
        UserResponse userResponseBooker = userService.createUser(userDtoBooker);
        ItemDto itemDto = ItemDto.builder().name("test1").description("test1").available(true).build();
        ItemDtoResponse itemDtoResponse = itemService.createItem(userResponseOwner.getId(), itemDto);
        LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        LocalDateTime startDate1 = currentDate.plusHours(1);
        LocalDateTime endDate1 = currentDate.plusHours(2);
        BookingDto bookingDto = BookingDto.builder().start(String.valueOf(startDate1)).end(String.valueOf(endDate1))
                .itemId(itemDtoResponse.getId()).bookerId(userResponseBooker.getId()).build();
        BookingDtoResponse bookingDtoResponse = bookingService.createBooking(userResponseBooker.getId(),bookingDto);
        List<BookingDtoResponse> bookingDtoResponseList = bookingService.getBookingByOwner(userResponseOwner.getId(), State.FUTURE, GetBookingParam.pageRequest(1,10));
        assertThat(bookingDtoResponseList.size(), notNullValue());
        assertThat(bookingDtoResponseList.get(0).getId(), equalTo(bookingDtoResponse.getId()));
        assertThat(bookingDtoResponseList.get(0).getStart(), equalTo(bookingDtoResponse.getStart()));
        assertThat(bookingDtoResponseList.get(0).getEnd(), equalTo(bookingDtoResponse.getEnd()));
        assertThat(bookingDtoResponseList.get(0).getStatus(), equalTo(bookingDtoResponse.getStatus()));
        assertThat(bookingDtoResponseList.get(0).getBooker().getId(), equalTo(bookingDtoResponse.getBooker().getId()));
        assertThat(bookingDtoResponseList.get(0).getItem().getId(), equalTo(bookingDtoResponse.getItem().getId()));
        assertThat(bookingDtoResponseList.get(0).getItem().getName(), equalTo(bookingDtoResponse.getItem().getName()));
    }

    @Test
    void testGetBookingByOwnerRejectedStatus() {
        UserDtoRequest userDtoBooker = UserDtoRequest.builder().name("testBooker").email("testBooker@test.ru").build();
        UserDtoRequest userDtoOwner = UserDtoRequest.builder().name("testOwner").email("testOwner@test.ru").build();
        UserResponse userResponseOwner = userService.createUser(userDtoOwner);
        UserResponse userResponseBooker = userService.createUser(userDtoBooker);
        ItemDto itemDto = ItemDto.builder().name("test1").description("test1").available(true).build();
        ItemDtoResponse itemDtoResponse = itemService.createItem(userResponseOwner.getId(), itemDto);
        LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        LocalDateTime startDate1 = currentDate.plusHours(1);
        LocalDateTime endDate1 = currentDate.plusHours(2);
        BookingDto bookingDto = BookingDto.builder().start(String.valueOf(startDate1)).end(String.valueOf(endDate1))
                .itemId(itemDtoResponse.getId()).bookerId(userResponseBooker.getId()).build();
        BookingDtoResponse bookingDtoResponse = bookingService.createBooking(userResponseBooker.getId(),bookingDto);
        bookingDtoResponse = bookingService.updateStatusBooking(userResponseOwner.getId(), bookingDtoResponse.getId(), false);
        List<BookingDtoResponse> bookingDtoResponseList = bookingService.getBookingByOwner(userResponseOwner.getId(), State.REJECTED, GetBookingParam.pageRequest(1,10));
        assertThat(bookingDtoResponseList.size(), notNullValue());
        assertThat(bookingDtoResponseList.get(0).getId(), equalTo(bookingDtoResponse.getId()));
        assertThat(bookingDtoResponseList.get(0).getStart(), equalTo(bookingDtoResponse.getStart()));
        assertThat(bookingDtoResponseList.get(0).getEnd(), equalTo(bookingDtoResponse.getEnd()));
        assertThat(bookingDtoResponseList.get(0).getStatus(), equalTo(bookingDtoResponse.getStatus()));
        assertThat(bookingDtoResponseList.get(0).getBooker().getId(), equalTo(bookingDtoResponse.getBooker().getId()));
        assertThat(bookingDtoResponseList.get(0).getItem().getId(), equalTo(bookingDtoResponse.getItem().getId()));
        assertThat(bookingDtoResponseList.get(0).getItem().getName(), equalTo(bookingDtoResponse.getItem().getName()));
    }

    @Test
    void testGetBookingByOwnerUnSupportedStatus() {
        UserDtoRequest userDtoBooker = UserDtoRequest.builder().name("testBooker").email("testBooker@test.ru").build();
        UserDtoRequest userDtoOwner = UserDtoRequest.builder().name("testOwner").email("testOwner@test.ru").build();
        UserResponse userResponseOwner = userService.createUser(userDtoOwner);
        UserResponse userResponseBooker = userService.createUser(userDtoBooker);
        ItemDto itemDto = ItemDto.builder().name("test1").description("test1").available(true).build();
        ItemDtoResponse itemDtoResponse = itemService.createItem(userResponseOwner.getId(), itemDto);
        LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        LocalDateTime startDate1 = currentDate.plusHours(1);
        LocalDateTime endDate1 = currentDate.plusHours(2);
        BookingDto bookingDto = BookingDto.builder().start(String.valueOf(startDate1)).end(String.valueOf(endDate1))
                .itemId(itemDtoResponse.getId()).bookerId(userResponseBooker.getId()).build();
        BookingDtoResponse bookingDtoResponse = bookingService.createBooking(userResponseBooker.getId(),bookingDto);
        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class, () -> bookingService.getBookingByOwner(userResponseOwner.getId(), State.UNSUPPORTED_STATUS, GetBookingParam.pageRequest(1,10)));

        Assertions.assertEquals("Unknown state: " + State.UNSUPPORTED_STATUS, exception.getMessage());
    }

    @Test
    void testGetBookingIdByAuthorOrOwner() {
        UserDtoRequest userDtoBooker = UserDtoRequest.builder().name("testBooker").email("testBooker@test.ru").build();
        UserDtoRequest userDtoOwner = UserDtoRequest.builder().name("testOwner").email("testOwner@test.ru").build();
        UserResponse userResponseOwner = userService.createUser(userDtoOwner);
        UserResponse userResponseBooker = userService.createUser(userDtoBooker);
        ItemDto itemDto = ItemDto.builder().name("test1").description("test1").available(true).build();
        ItemDtoResponse itemDtoResponse = itemService.createItem(userResponseOwner.getId(), itemDto);
        LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        LocalDateTime startDate1 = currentDate.plusHours(1);
        LocalDateTime endDate1 = currentDate.plusHours(2);
        BookingDto bookingDto = BookingDto.builder().start(String.valueOf(startDate1)).end(String.valueOf(endDate1))
                .itemId(itemDtoResponse.getId()).bookerId(userResponseBooker.getId()).build();
        BookingDtoResponse bookingDtoResponse = bookingService.createBooking(userResponseBooker.getId(),bookingDto);
        BookingDtoResponse bookingDtoResponseOwner = bookingService.getBookingIdByAuthorOrOwner(userResponseOwner.getId(), bookingDtoResponse.getId());
        assertThat(bookingDtoResponseOwner.getId(), equalTo(bookingDtoResponse.getId()));
        assertThat(bookingDtoResponseOwner.getStart(), equalTo(bookingDtoResponse.getStart()));
        assertThat(bookingDtoResponseOwner.getEnd(), equalTo(bookingDtoResponse.getEnd()));
        assertThat(bookingDtoResponseOwner.getStatus(), equalTo(bookingDtoResponse.getStatus()));
        assertThat(bookingDtoResponseOwner.getBooker().getId(), equalTo(bookingDtoResponse.getBooker().getId()));
        assertThat(bookingDtoResponseOwner.getItem().getId(), equalTo(bookingDtoResponse.getItem().getId()));
        assertThat(bookingDtoResponseOwner.getItem().getName(), equalTo(bookingDtoResponse.getItem().getName()));
    }

    @Test
    void testUpdateStatusBooking() {
        UserDtoRequest userDtoBooker = UserDtoRequest.builder().name("testBooker").email("testBooker@test.ru").build();
        UserDtoRequest userDtoOwner = UserDtoRequest.builder().name("testOwner").email("testOwner@test.ru").build();
        UserResponse userResponseOwner = userService.createUser(userDtoOwner);
        UserResponse userResponseBooker = userService.createUser(userDtoBooker);
        ItemDto itemDto = ItemDto.builder().name("test1").description("test1").available(true).build();
        ItemDtoResponse itemDtoResponse = itemService.createItem(userResponseOwner.getId(), itemDto);
        LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        LocalDateTime startDate1 = currentDate.plusHours(1);
        LocalDateTime endDate1 = currentDate.plusHours(2);
        BookingDto bookingDto = BookingDto.builder().start(String.valueOf(startDate1)).end(String.valueOf(endDate1))
                .itemId(itemDtoResponse.getId()).bookerId(userResponseBooker.getId()).build();
        BookingDtoResponse bookingDtoResponse = bookingService.createBooking(userResponseBooker.getId(),bookingDto);
        BookingDtoResponse bookingDtoResponseUpdate = bookingService.updateStatusBooking(userResponseOwner.getId(), bookingDtoResponse.getId(), true);
        assertThat(bookingDtoResponse.getId(), equalTo(bookingDtoResponseUpdate.getId()));
        assertThat(bookingDtoResponseUpdate.getStatus(), equalTo(Status.APPROVED));

    }
}
