package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    private final ItemDtoResponse itemDtoResponse = ItemDtoResponse.builder().id(1L).name("test")
            .description("test1").available(true).requestId(1L).build();
    @Autowired
    private MockMvc mvc;

    @Test
    void testCreateUser() throws Exception {
        when(itemService.createItem(anyLong(), any())).thenReturn(itemDtoResponse);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", "1");
        mvc.perform(post("/items").headers(headers)
                        .content(mapper.writeValueAsString(itemDtoResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoResponse.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoResponse.getDescription())))
                .andExpect(jsonPath("$.available",is(itemDtoResponse.getAvailable())))
                .andExpect(jsonPath("$.requestId",is(itemDtoResponse.getRequestId()), Long.class));
    }

    @Test
    void testCreateUserException() throws Exception {
        when(itemService.createItem(anyLong(), any())).thenReturn(itemDtoResponse);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", "");
        mvc.perform(post("/items").headers(headers)
                        .content(mapper.writeValueAsString(itemDtoResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void testUpdateItem() throws Exception {
        when(itemService.updateItem(anyLong(), any())).thenReturn(itemDtoResponse);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", "1");
        mvc.perform(patch("/items/{itemId}", "1").headers(headers)
                        .content(mapper.writeValueAsString(itemDtoResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoResponse.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoResponse.getDescription())))
                .andExpect(jsonPath("$.available",is(itemDtoResponse.getAvailable())))
                .andExpect(jsonPath("$.requestId",is(itemDtoResponse.getRequestId()), Long.class));
    }

    @Test
    void testFindItemId() throws Exception {
        when(itemService.getItemId(anyLong(), anyLong())).thenReturn(itemDtoResponse);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", "1");
        mvc.perform(get("/items/{itemId}", "1").headers(headers)
                        .content(mapper.writeValueAsString(itemDtoResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoResponse.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoResponse.getDescription())))
                .andExpect(jsonPath("$.available",is(itemDtoResponse.getAvailable())))
                .andExpect(jsonPath("$.requestId",is(itemDtoResponse.getRequestId()), Long.class));

    }

    @Test
    void findItemsByUserId() throws Exception {
        List<ItemDtoResponse> itemDtoResponseList = new ArrayList<>();
        List<CommentDtoResponse> commentDtoResponseList = new ArrayList<>();
        BookingDtoResponse.BookingDtoResponseOwner bookingDtoResponseLast = BookingDtoResponse.BookingDtoResponseOwner
                .builder().id(1L).bookerId(1L).build();
        BookingDtoResponse.BookingDtoResponseOwner bookingDtoResponseNext = BookingDtoResponse.BookingDtoResponseOwner
                .builder().id(2L).bookerId(1L).build();
        CommentDtoResponse commentDtoResponse = CommentDtoResponse.builder().id(1L)
                .text("test").authorName("test")
                .created(String.valueOf(LocalDateTime.of(2024,2, 9, 15,07))).build();
        itemDtoResponse.setLastBooking(bookingDtoResponseLast);
        itemDtoResponse.setNextBooking(bookingDtoResponseNext);
        commentDtoResponseList.add(commentDtoResponse);
        itemDtoResponse.setComments(commentDtoResponseList);
        itemDtoResponseList.add(itemDtoResponse);
        when(itemService.findItemsByUserId(anyLong(), any())).thenReturn(itemDtoResponseList);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", Long.toString(1L));
        mvc.perform(get("/items").headers(headers).param("from", "1").param("size", "10")
                        .content(mapper.writeValueAsString(itemDtoResponseList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemDtoResponseList.get(0).getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemDtoResponseList.get(0).getName())))
                .andExpect(jsonPath("$.[0].description", is(itemDtoResponseList.get(0).getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemDtoResponseList.get(0).getAvailable())))
                .andExpect(jsonPath("$.[0].lastBooking.id", is(itemDtoResponseList.get(0).getLastBooking().getId()), Long.class))
                .andExpect(jsonPath("$.[0].lastBooking.bookerId", is(itemDtoResponseList.get(0).getLastBooking().getBookerId()), Long.class))
                .andExpect(jsonPath("$.[0].nextBooking.id", is(itemDtoResponseList.get(0).getNextBooking().getId()), Long.class))
                .andExpect(jsonPath("$.[0].nextBooking.bookerId", is(itemDtoResponseList.get(0).getNextBooking().getBookerId()), Long.class))
                .andExpect(jsonPath("$.[0].comments.[0].id", is(itemDtoResponseList.get(0).getComments().get(0).getId()), Long.class))
                .andExpect(jsonPath("$.[0].comments.[0].text", is(itemDtoResponseList.get(0).getComments().get(0).getText())))
                .andExpect(jsonPath("$.[0].comments.[0].authorName", is(itemDtoResponseList.get(0).getComments().get(0).getAuthorName())))
                .andExpect(jsonPath("$.[0].comments.[0].created", is(itemDtoResponseList.get(0).getComments().get(0).getCreated())))
                .andExpect(jsonPath("$.[0].requestId", is(itemDtoResponseList.get(0).getRequestId()), Long.class));
    }

    @Test
    void findItemsByUserIdException() throws Exception {
        List<ItemDtoResponse> itemDtoResponseList = new ArrayList<>();
        List<CommentDtoResponse> commentDtoResponseList = new ArrayList<>();
        BookingDtoResponse.BookingDtoResponseOwner bookingDtoResponseLast = BookingDtoResponse.BookingDtoResponseOwner
                .builder().id(1L).bookerId(1L).build();
        BookingDtoResponse.BookingDtoResponseOwner bookingDtoResponseNext = BookingDtoResponse.BookingDtoResponseOwner
                .builder().id(2L).bookerId(1L).build();
        CommentDtoResponse commentDtoResponse = CommentDtoResponse.builder().id(1L)
                .text("test").authorName("test")
                .created(String.valueOf(LocalDateTime.of(2024, 2, 9, 15, 07))).build();
        itemDtoResponse.setLastBooking(bookingDtoResponseLast);
        itemDtoResponse.setNextBooking(bookingDtoResponseNext);
        commentDtoResponseList.add(commentDtoResponse);
        itemDtoResponse.setComments(commentDtoResponseList);
        itemDtoResponseList.add(itemDtoResponse);
        when(itemService.findItemsByUserId(anyLong(), any())).thenReturn(itemDtoResponseList);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", Long.toString(1L));
        mvc.perform(get("/items").headers(headers).param("from", "-1").param("size", "0")
                        .content(mapper.writeValueAsString(itemDtoResponseList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testSearchNameItemsAndDescription() throws Exception {
        List<ItemDtoResponse> itemDtoResponseList = new ArrayList<>();
        List<CommentDtoResponse> commentDtoResponseList = new ArrayList<>();
        BookingDtoResponse.BookingDtoResponseOwner bookingDtoResponseLast = BookingDtoResponse.BookingDtoResponseOwner
                .builder().id(1L).bookerId(1L).build();
        BookingDtoResponse.BookingDtoResponseOwner bookingDtoResponseNext = BookingDtoResponse.BookingDtoResponseOwner
                .builder().id(2L).bookerId(1L).build();
        CommentDtoResponse commentDtoResponse = CommentDtoResponse.builder().id(1L)
                .text("test").authorName("test")
                .created(String.valueOf(LocalDateTime.of(2024,2, 9, 15,07))).build();
        itemDtoResponse.setLastBooking(bookingDtoResponseLast);
        itemDtoResponse.setNextBooking(bookingDtoResponseNext);
        commentDtoResponseList.add(commentDtoResponse);
        itemDtoResponse.setComments(commentDtoResponseList);
        itemDtoResponseList.add(itemDtoResponse);
        when(itemService.searchNameItemsAndDescription(any(), any())).thenReturn(itemDtoResponseList);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", Long.toString(1L));
        mvc.perform(get("/items/search").headers(headers).param("text", "test").param("from", "1").param("size", "10")
                        .content(mapper.writeValueAsString(itemDtoResponseList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemDtoResponseList.get(0).getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemDtoResponseList.get(0).getName())))
                .andExpect(jsonPath("$.[0].description", is(itemDtoResponseList.get(0).getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemDtoResponseList.get(0).getAvailable())))
                .andExpect(jsonPath("$.[0].lastBooking.id", is(itemDtoResponseList.get(0).getLastBooking().getId()), Long.class))
                .andExpect(jsonPath("$.[0].lastBooking.bookerId", is(itemDtoResponseList.get(0).getLastBooking().getBookerId()), Long.class))
                .andExpect(jsonPath("$.[0].nextBooking.id", is(itemDtoResponseList.get(0).getNextBooking().getId()), Long.class))
                .andExpect(jsonPath("$.[0].nextBooking.bookerId", is(itemDtoResponseList.get(0).getNextBooking().getBookerId()), Long.class))
                .andExpect(jsonPath("$.[0].comments.[0].id", is(itemDtoResponseList.get(0).getComments().get(0).getId()), Long.class))
                .andExpect(jsonPath("$.[0].comments.[0].text", is(itemDtoResponseList.get(0).getComments().get(0).getText())))
                .andExpect(jsonPath("$.[0].comments.[0].authorName", is(itemDtoResponseList.get(0).getComments().get(0).getAuthorName())))
                .andExpect(jsonPath("$.[0].comments.[0].created", is(itemDtoResponseList.get(0).getComments().get(0).getCreated())))
                .andExpect(jsonPath("$.[0].requestId", is(itemDtoResponseList.get(0).getRequestId()), Long.class));
    }

    @Test
    void testSearchNameItemsAndDescriptionException() throws Exception {
        List<ItemDtoResponse> itemDtoResponseList = new ArrayList<>();
        List<CommentDtoResponse> commentDtoResponseList = new ArrayList<>();
        BookingDtoResponse.BookingDtoResponseOwner bookingDtoResponseLast = BookingDtoResponse.BookingDtoResponseOwner
                .builder().id(1L).bookerId(1L).build();
        BookingDtoResponse.BookingDtoResponseOwner bookingDtoResponseNext = BookingDtoResponse.BookingDtoResponseOwner
                .builder().id(2L).bookerId(1L).build();
        CommentDtoResponse commentDtoResponse = CommentDtoResponse.builder().id(1L)
                .text("test").authorName("test")
                .created(String.valueOf(LocalDateTime.of(2024, 2, 9, 15, 07))).build();
        itemDtoResponse.setLastBooking(bookingDtoResponseLast);
        itemDtoResponse.setNextBooking(bookingDtoResponseNext);
        commentDtoResponseList.add(commentDtoResponse);
        itemDtoResponse.setComments(commentDtoResponseList);
        itemDtoResponseList.add(itemDtoResponse);
        when(itemService.searchNameItemsAndDescription(any(), any())).thenReturn(itemDtoResponseList);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", Long.toString(1L));
        mvc.perform(get("/items/search").headers(headers).param("text", "test").param("from", "-1").param("size", "0")
                        .content(mapper.writeValueAsString(itemDtoResponseList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createCommentItem() throws Exception {
        CommentDtoResponse commentDtoResponse = CommentDtoResponse.builder().id(1L).text("test1").authorName("testAuthor")
                .created(String.valueOf(LocalDateTime.of(2024, 2, 9, 15, 07))).build();
        when(itemService.createCommentItem(anyLong(), anyLong(), any())).thenReturn(commentDtoResponse);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", "1");
        mvc.perform(post("/items/{itemId}/comment", "1").headers(headers)
                        .content(mapper.writeValueAsString(commentDtoResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDtoResponse.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDtoResponse.getAuthorName())))
                .andExpect(jsonPath("$.created",is(commentDtoResponse.getCreated())));
    }
}