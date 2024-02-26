package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.GetItemRequestParam;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.service.UserService;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.is;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceTest {

    private final EntityManager em;
    private final ItemRequestService itemRequestService;
    private final UserService userService;
    private final ItemService itemService;

    @Test
    void testCreateItemRequest() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().description("test").build();
        UserDtoRequest userDto = UserDtoRequest.builder().name("test1").email("test@test.ru").build();
        UserResponse userResponse = userService.createUser(userDto);
        itemRequestService.createItemRequest(userResponse.getId(), itemRequestDto);
        TypedQuery<ItemRequest> query = em.createQuery("Select i from ItemRequest i where i.description = :description", ItemRequest.class);
        ItemRequest itemRequest = query.setParameter("description", itemRequestDto.getDescription()).getSingleResult();
        assertThat(itemRequest.getId(), notNullValue());
        assertThat(itemRequest.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(itemRequest.getRequestor().getId(), equalTo(userResponse.getId()));
    }

    @Test
    void testFindItemRequestId() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().description("test").build();
        UserDtoRequest userDto = UserDtoRequest.builder().name("test1").email("test@test.ru").build();
        UserResponse userResponse = userService.createUser(userDto);
        LocalDateTime currentDateTime = LocalDateTime.now().withNano(0);
        ItemRequestResponse itemRequestResponse = itemRequestService.createItemRequest(userResponse.getId(), itemRequestDto);
        ItemDto itemDto = ItemDto.builder().name("Вещь").description("test")
                .available(true).requestId(itemRequestResponse.getId()).build();
        ItemDtoResponse itemDtoResponse = itemService.createItem(userResponse.getId(), itemDto);
        ItemRequestResponse.ItemRequestResponseItems itemRequestResponseItems = itemRequestService.findItemRequestId(userResponse.getId(), itemRequestResponse.getId());

        assertThat(itemRequestResponseItems.getId(), notNullValue());
        assertThat(itemRequestResponseItems.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(itemRequestResponseItems.getRequestor(), equalTo(userResponse.getId()));
        assertThat(itemRequestResponseItems.getCreated(), is(currentDateTime.toString()));
        assertThat(itemRequestResponseItems.getItems().get(0).getId(), equalTo(itemDtoResponse.getId()));
        assertThat(itemRequestResponseItems.getItems().get(0).getName(), equalTo(itemDtoResponse.getName()));
        assertThat(itemRequestResponseItems.getItems().get(0).getDescription(), equalTo(itemDtoResponse.getDescription()));
        assertThat(itemRequestResponseItems.getItems().get(0).getAvailable(), equalTo(itemDtoResponse.getAvailable()));
        assertThat(itemRequestResponseItems.getItems().get(0).getRequestId(), equalTo(itemDtoResponse.getRequestId()));
    }

    @Test
    void testFindItemRequest() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().description("test").build();
        UserDtoRequest userDto = UserDtoRequest.builder().name("test1").email("test@test.ru").build();
        UserResponse userResponse = userService.createUser(userDto);
        LocalDateTime currentDateTime = LocalDateTime.now().withNano(0);
        ItemRequestResponse itemRequestResponse = itemRequestService.createItemRequest(userResponse.getId(), itemRequestDto);
        ItemDto itemDto = ItemDto.builder().name("Вещь").description("test")
                .available(true).requestId(itemRequestResponse.getId()).build();
        ItemDtoResponse itemDtoResponse = itemService.createItem(userResponse.getId(), itemDto);
        List<ItemRequestResponse.ItemRequestResponseItems> itemRequestResponseItemsList = itemRequestService.findItemRequest(userResponse.getId());
        assertThat(itemRequestResponseItemsList.get(0).getId(), notNullValue());
        assertThat(itemRequestResponseItemsList.get(0).getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(itemRequestResponseItemsList.get(0).getRequestor(), equalTo(userResponse.getId()));
        assertThat(itemRequestResponseItemsList.get(0).getCreated(), is(currentDateTime.toString()));
        assertThat(itemRequestResponseItemsList.get(0).getItems().get(0).getId(), equalTo(itemDtoResponse.getId()));
        assertThat(itemRequestResponseItemsList.get(0).getItems().get(0).getName(), equalTo(itemDtoResponse.getName()));
        assertThat(itemRequestResponseItemsList.get(0).getItems().get(0).getDescription(), equalTo(itemDtoResponse.getDescription()));
        assertThat(itemRequestResponseItemsList.get(0).getItems().get(0).getAvailable(), equalTo(itemDtoResponse.getAvailable()));
        assertThat(itemRequestResponseItemsList.get(0).getItems().get(0).getRequestId(), equalTo(itemDtoResponse.getRequestId()));
    }

    @Test
    void testFindItemRequestAll() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().description("test").build();
        UserDtoRequest userDto = UserDtoRequest.builder().name("test1").email("test1@test.ru").build();
        UserDtoRequest userDtoOther = UserDtoRequest.builder().name("test2").email("test2@test.ru").build();
        UserResponse userResponse = userService.createUser(userDto);
        UserResponse userOtherResponse = userService.createUser(userDtoOther);
        LocalDateTime currentDateTime = LocalDateTime.now().withNano(0);
        ItemRequestResponse itemRequestResponse = itemRequestService.createItemRequest(userResponse.getId(), itemRequestDto);
        ItemDto itemDto = ItemDto.builder().name("Вещь").description("test")
                .available(true).requestId(itemRequestResponse.getId()).build();
        ItemDtoResponse itemDtoResponse = itemService.createItem(userResponse.getId(), itemDto);
        List<ItemRequestResponse.ItemRequestResponseItems> itemRequestResponseItemsList = itemRequestService.findItemRequestAll(userOtherResponse.getId(), GetItemRequestParam.pageRequest(1,  10));
        assertThat(itemRequestResponseItemsList.get(0).getId(), notNullValue());
        assertThat(itemRequestResponseItemsList.get(0).getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(itemRequestResponseItemsList.get(0).getRequestor(), equalTo(userResponse.getId()));
        assertThat(itemRequestResponseItemsList.get(0).getCreated(), is(currentDateTime.toString()));
        assertThat(itemRequestResponseItemsList.get(0).getItems().get(0).getId(), equalTo(itemDtoResponse.getId()));
        assertThat(itemRequestResponseItemsList.get(0).getItems().get(0).getName(), equalTo(itemDtoResponse.getName()));
        assertThat(itemRequestResponseItemsList.get(0).getItems().get(0).getDescription(), equalTo(itemDtoResponse.getDescription()));
        assertThat(itemRequestResponseItemsList.get(0).getItems().get(0).getAvailable(), equalTo(itemDtoResponse.getAvailable()));
        assertThat(itemRequestResponseItemsList.get(0).getItems().get(0).getRequestId(), equalTo(itemDtoResponse.getRequestId()));
    }
}