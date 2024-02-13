package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
class CommentRepositoryJpaTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private CommentRepositoryJpa commentRepositoryJpa;

    @Test
    void testFindByItemId() {
        User user = new User();
        user.setName("test");
        user.setEmail("test@test.ru");
        Item item = new Item();
        item.setName("test1");
        item.setDescription("test_test");
        item.setAvailable(true);
        em.persist(user);
        em.persist(item);
        item.setUser(user);
        Comment comment = new Comment();
        comment.setText("test");
        comment.setItem(item);
        comment.setAuthor("test1");
        comment.setCreated(LocalDateTime.now());
        em.persist(comment);
        List<Comment> commentList = commentRepositoryJpa.findByItemId(item.getId());
        assertThat(commentList.get(0).getId(), equalTo(comment.getId()));
        assertThat(commentList.get(0).getText(), equalTo(comment.getText()));
        assertThat(commentList.get(0).getCreated(), equalTo(comment.getCreated()));
    }
}