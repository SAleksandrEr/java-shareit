package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
@DataJpaTest
class ItemRepositoryJpaTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepositoryJpa itemRepositoryJpa;

    @Test
    void testUpdateItem() {
        User user = new User();
        user.setName("test");
        user.setEmail("test@test.ru");
        Item item = new Item();
        item.setName("test1");
        item.setDescription("test_test");
        item.setAvailable(true);
        item.setUser(user);
        em.persist(user);
        em.persist(item);
        item.setName("test2");
        item.setDescription("test_test2");
        item.setAvailable(false);
        itemRepositoryJpa.updateItem(item.getName(), item.getDescription(), item.getAvailable(), item.getId());
        Item itemEm = em.find(Item.class, item.getId());
        assertThat(itemEm.getId(), equalTo(item.getId()));
        assertThat(itemEm.getName(), equalTo(item.getName()));
        assertThat(itemEm.getDescription(), equalTo(item.getDescription()));
        assertThat(itemEm.getAvailable(), equalTo(item.getAvailable()));
    }

    @Test
    void testFindItemsByUserId() {
        User user = new User();
        user.setName("test");
        user.setEmail("test@test.ru");
        Item item = new Item();
        item.setName("test1");
        item.setDescription("test_test");
        item.setAvailable(true);
        item.setUser(user);
        em.persist(user);
        em.persist(item);
        List<Item> items = itemRepositoryJpa.findItemsByUserId(user.getId(), Pageable.ofSize(10)).getContent();
        TypedQuery<Item> query = em.getEntityManager()
                .createQuery("Select it from Item it where it.user.id = :id", Item.class);
        List<Item> itemEm = query.setParameter("id", user.getId()).getResultList();
        assertThat(itemEm.get(0).getId(), equalTo(items.get(0).getId()));
        assertThat(itemEm.get(0).getName(), equalTo(items.get(0).getName()));
        assertThat(itemEm.get(0).getDescription(), equalTo(items.get(0).getDescription()));
        assertThat(itemEm.get(0).getAvailable(), equalTo(items.get(0).getAvailable()));
        assertThat(itemEm.get(0).getUser(), equalTo(items.get(0).getUser()));
    }

    @Test
    void testFindByNameAndDescription() {
        Item item = new Item();
        item.setName("name");
        item.setDescription("TEST1");
        item.setAvailable(true);
        String query = "name";
        em.persist(item);
        Page<Item> items = itemRepositoryJpa.findByNameAndDescription(query, Pageable.ofSize(10));
        assertThat(item.getName(), equalTo(items.stream()
                .filter(item1 -> item1.getId().equals(item.getId())).collect(Collectors.toList()).get(0).getName()));
        query = "test";
        items = itemRepositoryJpa.findByNameAndDescription(query, Pageable.ofSize(10));
        assertThat(item.getName(), equalTo(items.stream()
                .filter(item1 -> item1.getId().equals(item.getId())).collect(Collectors.toList()).get(0).getName()));
    }

}