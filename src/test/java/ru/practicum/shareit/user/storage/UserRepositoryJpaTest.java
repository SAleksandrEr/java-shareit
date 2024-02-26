package ru.practicum.shareit.user.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.model.User;

import javax.persistence.TypedQuery;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
@DataJpaTest
class UserRepositoryJpaTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private UserRepositoryJpa userRepositoryJpa;

    @Test
    void testUpdateUser() {
        User user = new User();
        user.setName("test");
        user.setEmail("test@test.ru");
        em.persist(user);
        user.setName("Booker");
        user.setEmail("booker@test.ru");
        userRepositoryJpa.updateUser(user.getName(), user.getEmail(), user.getId());
        TypedQuery<User> query = em.getEntityManager()
                .createQuery("Select u from User u where u.id = :id", User.class);
        User userEm = query.setParameter("id", user.getId()).getSingleResult();
        assertThat(userEm.getId(), equalTo(user.getId()));
        assertThat(userEm.getName(), equalTo(user.getName()));
        assertThat(userEm.getEmail(), equalTo(user.getEmail()));
    }
}