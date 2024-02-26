package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.user.dto.UserResponse;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private final LocalDateTime currentDate = LocalDateTime.now().withNano(0);
    private final LocalDateTime startDate1 = currentDate.plusHours(1);
    private final LocalDateTime endDate1 = currentDate.plusHours(2);
    private final UserResponse.UserResponseBooking userResponseBooking = UserResponse.UserResponseBooking.builder().id(1L).build();
    private final  ItemDtoResponse.ItemDtoResponseBooking itemDtoResponseBooking = ItemDtoResponse.ItemDtoResponseBooking.builder()
            .id(1L).name("test").build();
    private final BookingDtoResponse bookingDtoResponse = BookingDtoResponse.builder().id(1L).start(String.valueOf(startDate1))
            .end(String.valueOf(endDate1)).status(Status.WAITING).booker(userResponseBooking).item(itemDtoResponseBooking).build();

    @Test
    void testCreateBooking() throws Exception {
        when(bookingService.createBooking(anyLong(), any())).thenReturn(bookingDtoResponse);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", "1");
        mvc.perform(post("/bookings").headers(headers)
                        .content(mapper.writeValueAsString(bookingDtoResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoResponse.getStart())))
                .andExpect(jsonPath("$.end", is(bookingDtoResponse.getEnd())))
                .andExpect(jsonPath("$.booker.id",is(bookingDtoResponse.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id",is(bookingDtoResponse.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name",is(bookingDtoResponse.getItem().getName())));
    }

    @Test
    void testUpdateBooking() throws Exception {
        when(bookingService.updateStatusBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDtoResponse);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", "1");
        mvc.perform(patch("/bookings/{bookingId}",1).headers(headers).param("approved", "true")
                        .content(mapper.writeValueAsString(bookingDtoResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoResponse.getStart())))
                .andExpect(jsonPath("$.end", is(bookingDtoResponse.getEnd())))
                .andExpect(jsonPath("$.booker.id",is(bookingDtoResponse.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id",is(bookingDtoResponse.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name",is(bookingDtoResponse.getItem().getName())));
    }

    @Test
    void testUpdateBookingException() throws Exception {
        when(bookingService.updateStatusBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDtoResponse);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", "1");
        mvc.perform(patch("/bookings/{bookingId}",0).headers(headers).param("approved", "true")
                        .content(mapper.writeValueAsString(bookingDtoResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testGetBookingIdByAuthorOrOwner() throws Exception {
        when(bookingService.getBookingIdByAuthorOrOwner(anyLong(), anyLong())).thenReturn(bookingDtoResponse);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", "1");
        mvc.perform(get("/bookings/{bookingId}",1).headers(headers)
                        .content(mapper.writeValueAsString(bookingDtoResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoResponse.getStart())))
                .andExpect(jsonPath("$.end", is(bookingDtoResponse.getEnd())))
                .andExpect(jsonPath("$.booker.id",is(bookingDtoResponse.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id",is(bookingDtoResponse.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name",is(bookingDtoResponse.getItem().getName())));

    }

    @Test
    void testGetBookingByCurrentUser() throws Exception {
        List<BookingDtoResponse> bookingDtoResponseList = new ArrayList<>();
        bookingDtoResponseList.add(bookingDtoResponse);
        when(bookingService.getBookingByCurrentUser(anyLong(), any(), any())).thenReturn(bookingDtoResponseList);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", "1");
        mvc.perform(get("/bookings/").headers(headers).param("state", "ALL").param("from", "1").param("size", "10")
                        .content(mapper.writeValueAsString(bookingDtoResponseList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(bookingDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start", is(bookingDtoResponse.getStart())))
                .andExpect(jsonPath("$.[0].end", is(bookingDtoResponse.getEnd())))
                .andExpect(jsonPath("$.[0].booker.id",is(bookingDtoResponse.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].item.id",is(bookingDtoResponse.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[0].item.name",is(bookingDtoResponse.getItem().getName())));
    }

    @Test
    void testGetBookingByCurrentUserException() throws Exception {
        List<BookingDtoResponse> bookingDtoResponseList = new ArrayList<>();
        bookingDtoResponseList.add(bookingDtoResponse);
        when(bookingService.getBookingByCurrentUser(anyLong(), any(), any())).thenReturn(bookingDtoResponseList);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", "1");
        mvc.perform(get("/bookings/").headers(headers).param("state", "ALL").param("from", "-1").param("size", "0")
                        .content(mapper.writeValueAsString(bookingDtoResponseList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testGetBookingByOwnerException() throws Exception {
        List<BookingDtoResponse> bookingDtoResponseList = new ArrayList<>();
        bookingDtoResponseList.add(bookingDtoResponse);
        when(bookingService.getBookingByOwner(anyLong(), any(), any())).thenReturn(bookingDtoResponseList);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", "1");
        mvc.perform(get("/bookings/owner").headers(headers).param("state", "ALL").param("from", "-1").param("size", "0")
                        .content(mapper.writeValueAsString(bookingDtoResponseList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testGetBookingByOwner() throws Exception {
        List<BookingDtoResponse> bookingDtoResponseList = new ArrayList<>();
        bookingDtoResponseList.add(bookingDtoResponse);
        when(bookingService.getBookingByOwner(anyLong(), any(), any())).thenReturn(bookingDtoResponseList);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", "1");
        mvc.perform(get("/bookings/owner").headers(headers).param("state", "ALL").param("from", "1").param("size", "10")
                        .content(mapper.writeValueAsString(bookingDtoResponseList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(bookingDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start", is(bookingDtoResponse.getStart())))
                .andExpect(jsonPath("$.[0].end", is(bookingDtoResponse.getEnd())))
                .andExpect(jsonPath("$.[0].booker.id",is(bookingDtoResponse.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].item.id",is(bookingDtoResponse.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[0].item.name",is(bookingDtoResponse.getItem().getName())));
    }
}