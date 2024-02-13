package ru.practicum.shareit.booking.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@DataJpaTest
class BookingRepositoryJpaTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private BookingRepositoryJpa bookingRepositoryJpa;

    @Test
    void testFindByBookingStartBeforeAndEndBefore() {
        User user = new User();
        user.setName("test");
        user.setEmail("test@test.ru");
        User userBooker = new User();
        userBooker.setName("Booker");
        userBooker.setEmail("booker@test.ru");
        Item item = new Item();
        item.setName("test1");
        item.setDescription("test_test");
        item.setAvailable(true);
        item.setUser(user);
        em.persist(user);
        em.persist(userBooker);
        em.persist(item);
        LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        LocalDateTime startDate1 = currentDate.plusHours(2);
        LocalDateTime endDate1 = currentDate.plusHours(3);
        LocalDateTime startDate2 = currentDate.plusHours(1);
        LocalDateTime endDate2 = currentDate.plusHours(4);
        Booking booking1 = new Booking();
        booking1.setStart(startDate1);
        booking1.setEnd(endDate1);
        booking1.setBooker(userBooker);
        booking1.setItem(item);
        booking1.setStatus(Status.WAITING);
        em.persist(booking1);
        List<Booking> bookingList = bookingRepositoryJpa.findByBookingStartBeforeAndEndBefore(startDate2, endDate2, Status.REJECTED, item.getId());
        assertThat(bookingList.size(), notNullValue());
    }

    @Test
    void testUpdateBooking() {
        User user = new User();
        user.setName("test");
        user.setEmail("test@test.ru");
        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@test.ru");
        Item item = new Item();
        item.setName("test1");
        item.setDescription("test_test");
        item.setAvailable(true);
        item.setUser(user);
        em.persist(user);
        em.persist(item);
        em.persist(booker);
        LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        LocalDateTime startDate1 = currentDate.plusHours(2);
        LocalDateTime endDate1 = currentDate.plusHours(3);
        Booking booking = new Booking();
        booking.setStart(startDate1);
        booking.setEnd(endDate1);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        em.persist(booking);
        int i = bookingRepositoryJpa.updateBooking(Status.APPROVED, booking.getId());
        TypedQuery<Booking> query = em.getEntityManager()
                .createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking bookingEm = query.setParameter("id", booking.getId()).getSingleResult();
        assertThat(bookingEm.getId(), equalTo(booking.getId()));
        assertThat(bookingEm.getStatus(), equalTo(Status.WAITING));
        assertThat(i, equalTo(1));
    }

    @Test
    void testFindByBookingIdAndOwner() {
        User userOwner = new User();
        userOwner.setName("test");
        userOwner.setEmail("test@test.ru");
        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@test.ru");
        Item item = new Item();
        item.setName("test1");
        item.setDescription("test_test");
        item.setAvailable(true);
        item.setUser(userOwner);
        em.persist(userOwner);
        em.persist(item);
        em.persist(booker);
        LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        LocalDateTime startDate1 = currentDate.plusHours(2);
        LocalDateTime endDate1 = currentDate.plusHours(3);
        Booking booking = new Booking();
        booking.setStart(startDate1);
        booking.setEnd(endDate1);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        em.persist(booking);
        Optional<Booking> bookingOwner = bookingRepositoryJpa.findByBookingIdAndOwner(booking.getId(), userOwner.getId());
        assertThat(bookingOwner, notNullValue());
        Optional<Booking> bookingBooker = bookingRepositoryJpa.findByBookingIdAndOwner(booking.getId(), booker.getId());
        assertThat(bookingBooker, equalTo(Optional.empty()));
    }

    @Test
    void testFindByBookingIdAndUserId() {
        User userOwner = new User();
        userOwner.setName("test");
        userOwner.setEmail("test@test.ru");
        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@test.ru");
        Item item = new Item();
        item.setName("test1");
        item.setDescription("test_test");
        item.setAvailable(true);
        item.setUser(userOwner);
        em.persist(userOwner);
        em.persist(item);
        em.persist(booker);
        LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        LocalDateTime startDate1 = currentDate.plusHours(2);
        LocalDateTime endDate1 = currentDate.plusHours(3);
        Booking booking = new Booking();
        booking.setStart(startDate1);
        booking.setEnd(endDate1);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        em.persist(booking);
        Optional<Booking> bookingOwner = bookingRepositoryJpa.findByBookingIdAndUserId(booking.getId(), userOwner.getId());
        assertThat(bookingOwner, notNullValue());
        Optional<Booking> bookingBooker = bookingRepositoryJpa.findByBookingIdAndUserId(booking.getId(), booker.getId());
        assertThat(bookingBooker, notNullValue());
    }

    @Test
    void testFindByBookerIdOrderByStartDesc() {
        User userOwner = new User();
        userOwner.setName("test");
        userOwner.setEmail("test@test.ru");
        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@test.ru");
        Item item = new Item();
        item.setName("test1");
        item.setDescription("test_test");
        item.setAvailable(true);
        item.setUser(userOwner);
        em.persist(userOwner);
        em.persist(item);
        em.persist(booker);
        LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        LocalDateTime startDate1 = currentDate.plusHours(2);
        LocalDateTime endDate1 = currentDate.plusHours(3);
        Booking booking = new Booking();
        booking.setStart(startDate1);
        booking.setEnd(endDate1);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        em.persist(booking);
        List<Booking> bookingList = bookingRepositoryJpa.findByBookerIdOrderByStartDesc(booker.getId(), Pageable.ofSize(10)).getContent();
        assertThat(bookingList.size(), notNullValue());
        assertThat(bookingList.get(0).getId(), equalTo(booking.getId()));
    }

    @Test
    void testFindByBookerIdAndEndAfterAndStartBeforeOrderByStartDesc() {
        User userOwner = new User();
        userOwner.setName("test");
        userOwner.setEmail("test@test.ru");
        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@test.ru");
        Item item = new Item();
        item.setName("test1");
        item.setDescription("test_test");
        item.setAvailable(true);
        item.setUser(userOwner);
        em.persist(userOwner);
        em.persist(item);
        em.persist(booker);
        LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        LocalDateTime startDate1 = currentDate.minusHours(1);
        LocalDateTime endDate1 = currentDate.plusHours(3);
        Booking booking = new Booking();
        booking.setStart(startDate1);
        booking.setEnd(endDate1);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        em.persist(booking);
        List<Booking> bookingList = bookingRepositoryJpa.findByBookerIdAndEndAfterAndStartBeforeOrderByStartDesc(booker.getId(), currentDate, currentDate);
        assertThat(bookingList.size(), notNullValue());
        assertThat(bookingList.get(0).getId(), equalTo(booking.getId()));
    }

    @Test
    void testFindByBookerIdAndEndBeforeOrderByStartDesc() {
        User userOwner = new User();
        userOwner.setName("test");
        userOwner.setEmail("test@test.ru");
        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@test.ru");
        Item item = new Item();
        item.setName("test1");
        item.setDescription("test_test");
        item.setAvailable(true);
        item.setUser(userOwner);
        em.persist(userOwner);
        em.persist(item);
        em.persist(booker);
        LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        LocalDateTime startDate1 = currentDate.minusHours(2);
        LocalDateTime endDate1 = currentDate.minusHours(1);
        Booking booking = new Booking();
        booking.setStart(startDate1);
        booking.setEnd(endDate1);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        em.persist(booking);
        List<Booking> bookingList = bookingRepositoryJpa.findByBookerIdAndEndBeforeOrderByStartDesc(booker.getId(), currentDate);
        assertThat(bookingList.size(), notNullValue());
        assertThat(bookingList.get(0).getId(), equalTo(booking.getId()));
    }

    @Test
    void testFindByBookerIdAndStartAfterAndEndAfterOrderByStartDesc() {
        User userOwner = new User();
        userOwner.setName("test");
        userOwner.setEmail("test@test.ru");
        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@test.ru");
        Item item = new Item();
        item.setName("test1");
        item.setDescription("test_test");
        item.setAvailable(true);
        item.setUser(userOwner);
        em.persist(userOwner);
        em.persist(item);
        em.persist(booker);
        LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        LocalDateTime startDate1 = currentDate.plusHours(1);
        LocalDateTime endDate1 = currentDate.plusHours(2);
        Booking booking = new Booking();
        booking.setStart(startDate1);
        booking.setEnd(endDate1);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        em.persist(booking);
        List<Booking> bookingList = bookingRepositoryJpa.findByBookerIdAndStartAfterAndEndAfterOrderByStartDesc(booker.getId(), currentDate, currentDate);
        assertThat(bookingList.size(), notNullValue());
        assertThat(bookingList.get(0).getId(), equalTo(booking.getId()));
    }

    @Test
    void testFindByBookerIdAndStatusOrderByStartDesc() {
        User userOwner = new User();
        userOwner.setName("test");
        userOwner.setEmail("test@test.ru");
        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@test.ru");
        Item item = new Item();
        item.setName("test1");
        item.setDescription("test_test");
        item.setAvailable(true);
        item.setUser(userOwner);
        em.persist(userOwner);
        em.persist(item);
        em.persist(booker);
        LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        LocalDateTime startDate1 = currentDate.plusHours(1);
        LocalDateTime endDate1 = currentDate.plusHours(2);
        Booking booking = new Booking();
        booking.setStart(startDate1);
        booking.setEnd(endDate1);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        em.persist(booking);
        List<Booking> bookingList = bookingRepositoryJpa.findByBookerIdAndStatusOrderByStartDesc(booker.getId(), Status.WAITING);
        assertThat(bookingList.size(), notNullValue());
        assertThat(bookingList.get(0).getId(), equalTo(booking.getId()));
        assertThat(bookingList.get(0).getStatus(), equalTo(booking.getStatus()));
    }

    @Test
    void testFindByItemUserIdOrderByStartDesc() {
        User userOwner = new User();
        userOwner.setName("test");
        userOwner.setEmail("test@test.ru");
        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@test.ru");
        Item item = new Item();
        item.setName("test1");
        item.setDescription("test_test");
        item.setAvailable(true);
        item.setUser(userOwner);
        em.persist(userOwner);
        em.persist(item);
        em.persist(booker);
        LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        LocalDateTime startDate1 = currentDate.plusHours(1);
        LocalDateTime endDate1 = currentDate.plusHours(2);
        Booking booking = new Booking();
        booking.setStart(startDate1);
        booking.setEnd(endDate1);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        em.persist(booking);
        List<Booking> bookingList = bookingRepositoryJpa.findByItemUserIdOrderByStartDesc(userOwner.getId(), Pageable.ofSize(10)).getContent();
        assertThat(bookingList.size(), notNullValue());
        assertThat(bookingList.get(0).getId(), equalTo(booking.getId()));
    }

    @Test
    void testFindByItemUserIdAndEndAfterAndStartBeforeOrderByStartDesc() {
        User userOwner = new User();
        userOwner.setName("test");
        userOwner.setEmail("test@test.ru");
        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@test.ru");
        Item item = new Item();
        item.setName("test1");
        item.setDescription("test_test");
        item.setAvailable(true);
        item.setUser(userOwner);
        em.persist(userOwner);
        em.persist(item);
        em.persist(booker);
        LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        LocalDateTime startDate1 = currentDate.minusHours(1);
        LocalDateTime endDate1 = currentDate.plusHours(1);
        Booking booking = new Booking();
        booking.setStart(startDate1);
        booking.setEnd(endDate1);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        em.persist(booking);
        List<Booking> bookingList = bookingRepositoryJpa.findByItemUserIdAndEndAfterAndStartBeforeOrderByStartDesc(userOwner.getId(), currentDate, currentDate);
        assertThat(bookingList.size(), notNullValue());
        assertThat(bookingList.get(0).getId(), equalTo(booking.getId()));
    }

    @Test
    void testFindByItemUserIdAndEndBeforeOrderByStartDesc() {
        User userOwner = new User();
        userOwner.setName("test");
        userOwner.setEmail("test@test.ru");
        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@test.ru");
        Item item = new Item();
        item.setName("test1");
        item.setDescription("test_test");
        item.setAvailable(true);
        item.setUser(userOwner);
        em.persist(userOwner);
        em.persist(item);
        em.persist(booker);
        LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        LocalDateTime startDate1 = currentDate.minusHours(2);
        LocalDateTime endDate1 = currentDate.minusHours(1);
        Booking booking = new Booking();
        booking.setStart(startDate1);
        booking.setEnd(endDate1);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        em.persist(booking);
        List<Booking> bookingList = bookingRepositoryJpa.findByItemUserIdAndEndBeforeOrderByStartDesc(userOwner.getId(), currentDate);
        assertThat(bookingList.size(), notNullValue());
        assertThat(bookingList.get(0).getId(), equalTo(booking.getId()));
    }

    @Test
    void testFindByItemUserIdAndStartAfterAndEndAfterOrderByStartDesc() {
        User userOwner = new User();
        userOwner.setName("test");
        userOwner.setEmail("test@test.ru");
        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@test.ru");
        Item item = new Item();
        item.setName("test1");
        item.setDescription("test_test");
        item.setAvailable(true);
        item.setUser(userOwner);
        em.persist(userOwner);
        em.persist(item);
        em.persist(booker);
        LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        LocalDateTime startDate1 = currentDate.plusHours(1);
        LocalDateTime endDate1 = currentDate.plusHours(2);
        Booking booking = new Booking();
        booking.setStart(startDate1);
        booking.setEnd(endDate1);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        em.persist(booking);
        List<Booking> bookingList = bookingRepositoryJpa.findByItemUserIdAndStartAfterAndEndAfterOrderByStartDesc(userOwner.getId(), currentDate, currentDate);
        assertThat(bookingList.size(), notNullValue());
        assertThat(bookingList.get(0).getId(), equalTo(booking.getId()));
    }

    @Test
    void testFindByItemUserIdAndStatusOrderByStartDesc() {
        User userOwner = new User();
        userOwner.setName("test");
        userOwner.setEmail("test@test.ru");
        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@test.ru");
        Item item = new Item();
        item.setName("test1");
        item.setDescription("test_test");
        item.setAvailable(true);
        item.setUser(userOwner);
        em.persist(userOwner);
        em.persist(item);
        em.persist(booker);
        LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        LocalDateTime startDate1 = currentDate.plusHours(1);
        LocalDateTime endDate1 = currentDate.plusHours(2);
        Booking booking = new Booking();
        booking.setStart(startDate1);
        booking.setEnd(endDate1);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        em.persist(booking);
        List<Booking> bookingList = bookingRepositoryJpa.findByItemUserIdAndStatusOrderByStartDesc(userOwner.getId(), Status.WAITING);
        assertThat(bookingList.size(), notNullValue());
        assertThat(bookingList.get(0).getId(), equalTo(booking.getId()));
        assertThat(bookingList.get(0).getStatus(), equalTo(booking.getStatus()));
    }

    @Test
    void testFindByItemId() {
        User userOwner = new User();
        userOwner.setName("test");
        userOwner.setEmail("test@test.ru");
        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@test.ru");
        Item item = new Item();
        item.setName("test1");
        item.setDescription("test_test");
        item.setAvailable(true);
        item.setUser(userOwner);
        em.persist(userOwner);
        em.persist(item);
        em.persist(booker);
        LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        LocalDateTime startDate1 = currentDate.plusHours(1);
        LocalDateTime endDate1 = currentDate.plusHours(2);
        Booking booking = new Booking();
        booking.setStart(startDate1);
        booking.setEnd(endDate1);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        em.persist(booking);
        List<Booking> bookingList = bookingRepositoryJpa.findByItemId(item.getId());
        assertThat(bookingList.size(), notNullValue());
        assertThat(bookingList.get(0).getId(), equalTo(booking.getId()));
    }

    @Test
    void testFindByItemIdAndBookerIdAndEndBefore() {
        User userOwner = new User();
        userOwner.setName("test");
        userOwner.setEmail("test@test.ru");
        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@test.ru");
        Item item = new Item();
        item.setName("test1");
        item.setDescription("test_test");
        item.setAvailable(true);
        item.setUser(userOwner);
        em.persist(userOwner);
        em.persist(item);
        em.persist(booker);
        LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        LocalDateTime startDate1 = currentDate.minusHours(2);
        LocalDateTime endDate1 = currentDate.minusHours(1);
        Booking booking = new Booking();
        booking.setStart(startDate1);
        booking.setEnd(endDate1);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        em.persist(booking);
        List<Booking> bookingList = bookingRepositoryJpa.findByItemIdAndBookerIdAndEndBefore(item.getId(), booker.getId(), currentDate);
        assertThat(bookingList.size(), notNullValue());
        assertThat(bookingList.get(0).getId(), equalTo(booking.getId()));
    }
}