package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    ObjectMapper mapper;
    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .description("").build();
    private final ItemRequestResponse itemRequestResponse = ItemRequestResponse.builder().id(1L)
            .description("Хотел бы воспользоваться щёткой для обуви")
            .requestor(1L).created(String.valueOf(LocalDateTime.of(2024,2, 9, 15,07)))
            .build();

    @Test
    void createItemRequest() throws Exception {
     when(itemRequestService.createItemRequest(anyLong(), any())).thenReturn(itemRequestResponse);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", "1");
        mvc.perform(post("/requests").headers(headers)
                        .content(mapper.writeValueAsString(itemRequestResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestResponse.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestResponse.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestResponse.getCreated())));
    }

    @Test
    void createItemRequestException() throws Exception {
        when(itemRequestService.createItemRequest(anyLong(), any())).thenReturn(itemRequestResponse);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", "1");
        mvc.perform(post("/requests").headers(headers)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void findIItemRequest() throws Exception {
        List<ItemRequestResponse.ItemRequestResponseItems> itemRequestResponseItemsList = new ArrayList<>();
        List<ItemDtoResponse.ItemDtoResponseRequest> itemDtoResponseRequestlist = new ArrayList<>();
        ItemDtoResponse.ItemDtoResponseRequest itemDtoResponseRequest = ItemDtoResponse.ItemDtoResponseRequest.builder()
                .id(1L).name("test1").description("test1").available(true).requestId(1L).build();
        itemDtoResponseRequestlist.add(itemDtoResponseRequest);
        ItemRequestResponse.ItemRequestResponseItems itemRequestResponseItems = ItemRequestResponse.ItemRequestResponseItems
                        .builder().id(1L).description("test").requestor(1L).created(String.valueOf(LocalDateTime.of(2024,2, 9, 15,07)))
                        .items(itemDtoResponseRequestlist).build();
        itemRequestResponseItemsList.add(itemRequestResponseItems);
        when(itemRequestService.findItemRequest(anyLong())).thenReturn(itemRequestResponseItemsList);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", Long.toString(1L));
        mvc.perform(get("/requests").headers(headers)
                        .content(mapper.writeValueAsString(itemRequestResponseItemsList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemRequestResponseItemsList.get(0).getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(itemRequestResponseItemsList.get(0).getDescription())))
                .andExpect(jsonPath("$.[0].requestor", is(itemRequestResponseItemsList.get(0).getRequestor().intValue())))
                .andExpect(jsonPath("$.[0].created", is(itemRequestResponseItemsList.get(0).getCreated())))
                .andExpect(jsonPath("$.[0].items.[0].id", is(itemRequestResponseItemsList.get(0).getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$.[0].items.[0].name", is(itemRequestResponseItemsList.get(0).getItems().get(0).getName())))
                .andExpect(jsonPath("$.[0].items.[0].description", is(itemRequestResponseItemsList.get(0).getItems().get(0).getDescription())))
                .andExpect(jsonPath("$.[0].items.[0].available", is(itemRequestResponseItemsList.get(0).getItems().get(0).getAvailable())))
                .andExpect(jsonPath("$.[0].items.[0].requestId", is(itemRequestResponseItemsList.get(0).getItems().get(0).getRequestId()), Long.class));

    }

    @Test
    void findIItemRequestAll() throws Exception {
        List<ItemRequestResponse.ItemRequestResponseItems> itemRequestResponseItemsList = new ArrayList<>();
        List<ItemDtoResponse.ItemDtoResponseRequest> itemDtoResponseRequestlist = new ArrayList<>();
        ItemDtoResponse.ItemDtoResponseRequest itemDtoResponseRequest = ItemDtoResponse.ItemDtoResponseRequest.builder()
                .id(1L).name("test1").description("test1").available(true).requestId(1L).build();
        itemDtoResponseRequestlist.add(itemDtoResponseRequest);
        ItemRequestResponse.ItemRequestResponseItems itemRequestResponseItems = ItemRequestResponse.ItemRequestResponseItems
                .builder().id(1L).description("test").requestor(1L).created(String.valueOf(LocalDateTime.of(2024,2, 9, 15,07)))
                .items(itemDtoResponseRequestlist).build();
        itemRequestResponseItemsList.add(itemRequestResponseItems);
        when(itemRequestService.findItemRequestAll(anyLong(), any())).thenReturn(itemRequestResponseItemsList);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", Long.toString(1L));
        mvc.perform(get("/requests/all").headers(headers).param("from", "1").param("size", "10")
                        .content(mapper.writeValueAsString(itemRequestResponseItemsList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemRequestResponseItemsList.get(0).getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(itemRequestResponseItemsList.get(0).getDescription())))
                .andExpect(jsonPath("$.[0].requestor", is(itemRequestResponseItemsList.get(0).getRequestor()), Long.class))
                .andExpect(jsonPath("$.[0].created", is(itemRequestResponseItemsList.get(0).getCreated())))
                .andExpect(jsonPath("$.[0].items.[0].id", is(itemRequestResponseItemsList.get(0).getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$.[0].items.[0].name", is(itemRequestResponseItemsList.get(0).getItems().get(0).getName())))
                .andExpect(jsonPath("$.[0].items.[0].description", is(itemRequestResponseItemsList.get(0).getItems().get(0).getDescription())))
                .andExpect(jsonPath("$.[0].items.[0].available", is(itemRequestResponseItemsList.get(0).getItems().get(0).getAvailable())))
                .andExpect(jsonPath("$.[0].items.[0].requestId", is(itemRequestResponseItemsList.get(0).getItems().get(0).getRequestId()), Long.class));

    }

    @Test
    void findIItemRequestAllException() throws Exception {
        List<ItemRequestResponse.ItemRequestResponseItems> itemRequestResponseItemsList = new ArrayList<>();
        List<ItemDtoResponse.ItemDtoResponseRequest> itemDtoResponseRequestlist = new ArrayList<>();
        ItemDtoResponse.ItemDtoResponseRequest itemDtoResponseRequest = ItemDtoResponse.ItemDtoResponseRequest.builder()
                .id(1L).name("test1").description("test1").available(true).requestId(1L).build();
        itemDtoResponseRequestlist.add(itemDtoResponseRequest);
        ItemRequestResponse.ItemRequestResponseItems itemRequestResponseItems = ItemRequestResponse.ItemRequestResponseItems
                .builder().id(1L).description("test").requestor(1L).created(String.valueOf(LocalDateTime.of(2024,2, 9, 15,07)))
                .items(itemDtoResponseRequestlist).build();
        itemRequestResponseItemsList.add(itemRequestResponseItems);
        when(itemRequestService.findItemRequestAll(anyLong(), any())).thenReturn(itemRequestResponseItemsList);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", Long.toString(1L));
        mvc.perform(get("/requests/all").headers(headers).param("from", "-1").param("size", "0")
                        .content(mapper.writeValueAsString(itemRequestResponseItemsList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void findItemRequestId() throws Exception {
        List<ItemDtoResponse.ItemDtoResponseRequest> itemDtoResponseRequestlist = new ArrayList<>();
        ItemDtoResponse.ItemDtoResponseRequest itemDtoResponseRequest = ItemDtoResponse.ItemDtoResponseRequest.builder()
                .id(1L).name("test1").description("test1").available(true).requestId(1L).build();
        itemDtoResponseRequestlist.add(itemDtoResponseRequest);
        ItemRequestResponse.ItemRequestResponseItems itemRequestResponseItems = ItemRequestResponse.ItemRequestResponseItems
                .builder().id(1L).description("test").requestor(1L).created(String.valueOf(LocalDateTime.of(2024,2, 9, 15,07)))
                .items(itemDtoResponseRequestlist).build();
        when(itemRequestService.findItemRequestId(anyLong(), anyLong())).thenReturn(itemRequestResponseItems);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", Long.toString(1L));
        mvc.perform(get("/requests/{requestId}", "1").headers(headers)
                        .content(mapper.writeValueAsString(itemRequestResponseItems))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestResponseItems.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestResponseItems.getDescription())))
                .andExpect(jsonPath("$.requestor", is(itemRequestResponseItems.getRequestor()), Long.class))
                .andExpect(jsonPath("$.created", is(itemRequestResponseItems.getCreated())))
                .andExpect(jsonPath("$.items.[0].id", is(itemRequestResponseItems.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$.items.[0].name", is(itemRequestResponseItems.getItems().get(0).getName())))
                .andExpect(jsonPath("$.items.[0].description", is(itemRequestResponseItems.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$.items.[0].available", is(itemRequestResponseItems.getItems().get(0).getAvailable())))
                .andExpect(jsonPath("$.items.[0].requestId", is(itemRequestResponseItems.getItems().get(0).getRequestId()), Long.class));

    }
}