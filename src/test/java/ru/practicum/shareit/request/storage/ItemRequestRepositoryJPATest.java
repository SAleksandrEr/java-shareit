package ru.practicum.shareit.request.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;


@DataJpaTest
class ItemRequestRepositoryJPATest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRequestRepositoryJPA itemRequestRepositoryJPA;

    @Test
    void findAllByRequestorIdOrderByCreatedDesc() {
        User user = new User();
        user.setName("test");
        user.setEmail("test@test.ru");
        em.persist(user);
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Хотел бы воспользоваться щёткой для обуви");
        itemRequest.setRequestor(user);
        em.persist(itemRequest);
        List<ItemRequest> itemRequests = itemRequestRepositoryJPA.findAllByRequestorIdOrderByCreatedDesc(user.getId());
        assertThat(itemRequest.getId(), notNullValue());
        assertThat(itemRequest.getDescription(), equalTo(itemRequests.get(0).getDescription()));
    }

    @Test
    void findAllByRequestorIdNotOrderByCreatedDesc() {
        User user1 = new User();
        user1.setName("test1");
        user1.setEmail("test1@test.ru");
        em.persist(user1);
        User user2 = new User();
        user2.setName("test2");
        user2.setEmail("test2@test.ru");
        em.persist(user2);
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Хотел бы воспользоваться щёткой для обуви");
        itemRequest.setRequestor(user1);
        em.persist(itemRequest);
        Page<ItemRequest> itemRequests = itemRequestRepositoryJPA.findAllByRequestorIdNotOrderByCreatedDesc(user2.getId(), Pageable.ofSize(10));
        assertThat(itemRequest.getId(), notNullValue());
        assertThat(itemRequest.getDescription(), equalTo(itemRequests.stream()
                .filter(itemRequest1 -> itemRequest1.getId().equals(itemRequest.getId()))
                .collect(Collectors.toList()).get(0).getDescription()));
    }
}