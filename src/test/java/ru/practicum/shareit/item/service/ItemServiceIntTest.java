package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceIntTest {
    private final EntityManager em;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;

    @Test
    void testUpdateItem() {
        ItemDto itemDto = ItemDto.builder().name("test1").description("test1").available(true).requestId(1L).build();
        UserDtoRequest userDto = UserDtoRequest.builder().name("test1").email("test@test.ru").build();
        UserResponse userResponse = userService.createUser(userDto);
        ItemDtoResponse itemDtoResponse = itemService.createItem(userResponse.getId(), itemDto);
        ItemDtoPatch itemDtoPatch = ItemDtoPatch.builder().id(itemDtoResponse.getId()).name("test2").description("test2").available(false).build();
        itemService.updateItem(userResponse.getId(), itemDtoPatch);
        TypedQuery<Item> query = em.createQuery("Select it from Item it where it.id = :id", Item.class);
        Item item = query.setParameter("id", itemDtoResponse.getId()).getSingleResult();
        assertThat(item.getId(), notNullValue());
        assertThat(item.getDescription(), equalTo(itemDtoPatch.getDescription()));
        assertThat(item.getName(), equalTo(itemDtoPatch.getName()));
        assertThat(item.getAvailable(), equalTo(itemDtoPatch.getAvailable()));
    }

    @Test
    void testFindItemsByUserId() throws InterruptedException {
        ItemDto itemDto = ItemDto.builder().name("test1").description("test1").available(true).build();
        UserDtoRequest userDtoOwner = UserDtoRequest.builder().name("testOwner").email("testOwner@test.ru").build();
        UserDtoRequest userDtoBooker = UserDtoRequest.builder().name("testBooker").email("testBooker@test.ru").build();
        UserResponse userResponseOwner = userService.createUser(userDtoOwner);
        UserResponse userResponseBooker = userService.createUser(userDtoBooker);
        ItemDtoResponse itemDtoResponse = itemService.createItem(userResponseOwner.getId(), itemDto);
        CommentDto commentDto = CommentDto.builder().text("test1").itemId(itemDtoResponse.getId())
                .authorId(userResponseBooker.getId()).build();
        LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        LocalDateTime startDate1 = currentDate.plusSeconds(1);
        LocalDateTime endDate1 = currentDate.plusSeconds(2);
        LocalDateTime startDate2 = currentDate.plusDays(3);
        LocalDateTime endDate2 = currentDate.plusDays(4);
        BookingDto bookingDto1 = BookingDto.builder().start(String.valueOf(startDate1)).end(String.valueOf(endDate1))
                .itemId(itemDtoResponse.getId()).bookerId(userResponseBooker.getId()).build();
        BookingDto bookingDto2 = BookingDto.builder().start(String.valueOf(startDate2)).end(String.valueOf(endDate2))
                .itemId(itemDtoResponse.getId()).bookerId(userResponseBooker.getId()).build();
        BookingDtoResponse bookingDtoResponse1 = bookingService.createBooking(userResponseBooker.getId(),bookingDto1);
        BookingDtoResponse bookingDtoResponse2 = bookingService.createBooking(userResponseBooker.getId(),bookingDto2);
        Thread.sleep(20_00);
        CommentDtoResponse commentDtoResponse = itemService.createCommentItem(userResponseBooker.getId(), itemDtoResponse.getId(), commentDto);
        List<ItemDtoResponse> itemDtoResponseList = itemService.findItemsByUserId(userResponseOwner.getId(), GetItemParam.pageRequest(1, 10));
        assertThat(itemDtoResponseList.size(), notNullValue());
        assertThat(itemDtoResponseList.get(0).getId(), equalTo(itemDtoResponse.getId()));
        assertThat(itemDtoResponseList.get(0).getName(), equalTo(itemDtoResponse.getName()));
        assertThat(itemDtoResponseList.get(0).getDescription(), equalTo(itemDtoResponse.getDescription()));
        assertThat(itemDtoResponseList.get(0).getAvailable(), equalTo(itemDtoResponse.getAvailable()));
        assertThat(itemDtoResponseList.get(0).getLastBooking().getId(), equalTo(bookingDtoResponse1.getId()));
        assertThat(itemDtoResponseList.get(0).getLastBooking().getBookerId(), equalTo(bookingDtoResponse1.getBooker().getId()));
        assertThat(itemDtoResponseList.get(0).getNextBooking().getId(), equalTo(bookingDtoResponse2.getId()));
        assertThat(itemDtoResponseList.get(0).getNextBooking().getBookerId(), equalTo(bookingDtoResponse2.getBooker().getId()));
        assertThat(itemDtoResponseList.get(0).getComments().get(0).getId(), equalTo(commentDtoResponse.getId()));
        assertThat(itemDtoResponseList.get(0).getComments().get(0).getText(), equalTo(commentDtoResponse.getText()));
        assertThat(itemDtoResponseList.get(0).getComments().get(0).getAuthorName(), equalTo(commentDtoResponse.getAuthorName()));
        assertThat(itemDtoResponseList.get(0).getComments().get(0).getCreated(), equalTo(commentDtoResponse.getCreated()));
    }

    @Test
    void testGetItemId() throws InterruptedException {
        ItemDto itemDto = ItemDto.builder().name("test1").description("test1").available(true).build();
        UserDtoRequest userDtoOwner = UserDtoRequest.builder().name("testOwner").email("testOwner@test.ru").build();
        UserDtoRequest userDtoBooker = UserDtoRequest.builder().name("testBooker").email("testBooker@test.ru").build();
        UserResponse userResponseOwner = userService.createUser(userDtoOwner);
        UserResponse userResponseBooker = userService.createUser(userDtoBooker);
        ItemDtoResponse itemDtoResponse = itemService.createItem(userResponseOwner.getId(), itemDto);
        CommentDto commentDto = CommentDto.builder().text("test1").itemId(itemDtoResponse.getId())
                .authorId(userResponseBooker.getId()).build();
        LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        LocalDateTime startDate1 = currentDate.plusSeconds(1);
        LocalDateTime endDate1 = currentDate.plusSeconds(2);
        LocalDateTime startDate2 = currentDate.plusDays(3);
        LocalDateTime endDate2 = currentDate.plusDays(4);
        BookingDto bookingDto1 = BookingDto.builder().start(String.valueOf(startDate1)).end(String.valueOf(endDate1))
                .itemId(itemDtoResponse.getId()).bookerId(userResponseBooker.getId()).build();
        BookingDto bookingDto2 = BookingDto.builder().start(String.valueOf(startDate2)).end(String.valueOf(endDate2))
                .itemId(itemDtoResponse.getId()).bookerId(userResponseBooker.getId()).build();
        BookingDtoResponse bookingDtoResponse1 = bookingService.createBooking(userResponseBooker.getId(),bookingDto1);
        BookingDtoResponse bookingDtoResponse2 = bookingService.createBooking(userResponseBooker.getId(),bookingDto2);
        Thread.sleep(20_00);
        CommentDtoResponse commentDtoResponse = itemService.createCommentItem(userResponseBooker.getId(), itemDtoResponse.getId(), commentDto);
        ItemDtoResponse itemDtoResponseNew = itemService.getItemId(userResponseOwner.getId(), itemDtoResponse.getId());
        assertThat(itemDtoResponseNew.getId(), equalTo(itemDtoResponse.getId()));
        assertThat(itemDtoResponseNew.getName(), equalTo(itemDtoResponse.getName()));
        assertThat(itemDtoResponseNew.getDescription(), equalTo(itemDtoResponse.getDescription()));
        assertThat(itemDtoResponseNew.getAvailable(), equalTo(itemDtoResponse.getAvailable()));
        assertThat(itemDtoResponseNew.getLastBooking().getId(), equalTo(bookingDtoResponse1.getId()));
        assertThat(itemDtoResponseNew.getLastBooking().getBookerId(), equalTo(bookingDtoResponse1.getBooker().getId()));
        assertThat(itemDtoResponseNew.getNextBooking().getId(), equalTo(bookingDtoResponse2.getId()));
        assertThat(itemDtoResponseNew.getNextBooking().getBookerId(), equalTo(bookingDtoResponse2.getBooker().getId()));
        assertThat(itemDtoResponseNew.getComments().get(0).getId(), equalTo(commentDtoResponse.getId()));
        assertThat(itemDtoResponseNew.getComments().get(0).getText(), equalTo(commentDtoResponse.getText()));
        assertThat(itemDtoResponseNew.getComments().get(0).getAuthorName(), equalTo(commentDtoResponse.getAuthorName()));
        assertThat(itemDtoResponseNew.getComments().get(0).getCreated(), equalTo(commentDtoResponse.getCreated()));
    }

    @Test
    void searchNameItemsAndDescription() {
        ItemDto itemDto = ItemDto.builder().name("test1").description("test1").available(true).build();
        UserDtoRequest userDtoOwner = UserDtoRequest.builder().name("testOwner").email("testOwner@test.ru").build();
        UserResponse userResponseOwner = userService.createUser(userDtoOwner);
        ItemDtoResponse itemDtoResponse = itemService.createItem(userResponseOwner.getId(), itemDto);
        List<ItemDtoResponse> itemDtoResponseList = itemService.searchNameItemsAndDescription("TeSt", GetItemParam.pageRequest(1, 10));
        assertThat(itemDtoResponseList.size(), notNullValue());
        assertThat(itemDtoResponseList.get(0).getId(), equalTo(itemDtoResponse.getId()));
        assertThat(itemDtoResponseList.get(0).getName(), equalTo(itemDtoResponse.getName()));
        assertThat(itemDtoResponseList.get(0).getDescription(), equalTo(itemDtoResponse.getDescription()));
        assertThat(itemDtoResponseList.get(0).getAvailable(), equalTo(itemDtoResponse.getAvailable()));
    }

    @Test
    void searchNameItemsAndDescriptionNull() {
        ItemDto itemDto = ItemDto.builder().name("test1").description("test1").available(true).build();
        UserDtoRequest userDtoOwner = UserDtoRequest.builder().name("testOwner").email("testOwner@test.ru").build();
        UserResponse userResponseOwner = userService.createUser(userDtoOwner);
        itemService.createItem(userResponseOwner.getId(), itemDto);
        List<ItemDtoResponse> itemDtoResponseList = itemService.searchNameItemsAndDescription("", GetItemParam.pageRequest(1, 10));
        assertThat(itemDtoResponseList.size(), equalTo(0));
    }

    @Test
    void testCreateCommentItem() throws InterruptedException {
        ItemDto itemDto = ItemDto.builder().name("test1").description("test1").available(true).build();
        UserDtoRequest userDtoOwner = UserDtoRequest.builder().name("testOwner").email("testOwner@test.ru").build();
        UserDtoRequest userDtoBooker = UserDtoRequest.builder().name("testBooker").email("testBooker@test.ru").build();
        UserResponse userResponseOwner = userService.createUser(userDtoOwner);
        UserResponse userResponseBooker = userService.createUser(userDtoBooker);
        ItemDtoResponse itemDtoResponse = itemService.createItem(userResponseOwner.getId(), itemDto);
        CommentDto commentDto = CommentDto.builder().text("test1").itemId(itemDtoResponse.getId())
                .authorId(userResponseBooker.getId()).build();
        LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        LocalDateTime startDate1 = currentDate.plusSeconds(1);
        LocalDateTime endDate1 = currentDate.plusSeconds(2);
        BookingDto bookingDto = BookingDto.builder().start(String.valueOf(startDate1)).end(String.valueOf(endDate1))
                .itemId(itemDtoResponse.getId()).bookerId(userResponseBooker.getId()).build();
        bookingService.createBooking(userResponseBooker.getId(),bookingDto);
        Thread.sleep(20_00);
        CommentDtoResponse commentDtoResponse = itemService.createCommentItem(userResponseBooker.getId(), itemDtoResponse.getId(), commentDto);
        ItemDtoResponse itemDtoResponseNew = itemService.getItemId(userResponseOwner.getId(), itemDtoResponse.getId());
        assertThat(commentDtoResponse.getId(), equalTo(itemDtoResponseNew.getComments().get(0).getId()));
        assertThat(commentDtoResponse.getText(), equalTo(itemDtoResponseNew.getComments().get(0).getText()));
        assertThat(commentDtoResponse.getAuthorName(), equalTo(itemDtoResponseNew.getComments().get(0).getAuthorName()));
        assertThat(commentDtoResponse.getCreated(), equalTo(itemDtoResponseNew.getComments().get(0).getCreated()));
    }
}
