package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepositoryJpa extends JpaRepository<Booking, Long> {

    @Query("select b " +
            "from Booking as b " +
            "where (b.item.id = ?4 " +
            "AND NOT b.status = ?3 " +
            "AND b.start > ?1 " +
            "and b.start < ?2) " +
            "OR (b.item.id = ?4 " +
            "AND NOT b.status = ?3 " +
            "AND b.start < ?1 " +
            "and b.end > ?2) " +
            "OR (b.item.id = ?4 " +
            "AND NOT b.status = ?3 " +
            "AND b.end > ?1 " +
            "AND b.end < ?2) " +
            "OR (b.item.id = ?4 " +
            "AND NOT b.status = ?3 " +
            "AND b.start > ?1 " +
            "AND b.end < ?2) ")
    List<Booking> findByBookingStartBeforeAndEndBefore(LocalDateTime startDate, LocalDateTime endDate, Status status, Long itemId);

    @Modifying
    @Query("UPDATE Booking as b " +
            "SET b.status = ?1 " +
            "where b.id = ?2 " +
            "AND NOT b.status = 'APPROVED'")
    Integer updateBooking(Status status, Long bookingId);

    @Query("select b " +
            "from Booking as b " +
            "LEFT JOIN Item as it ON b.item.id = it.id " +
            "LEFT JOIN User as u ON it.user.id = u.id " +
            "where b.id = ?1 " +
            "AND u.id = ?2")
    Optional<Booking> findByBookingIdAndOwner(Long bookingId, Long userId);

    @Query("select b " +
            "from Booking as b " +
            "LEFT JOIN Item as it ON b.item.id = it.id " +
            "LEFT JOIN User as u ON it.user.id = u.id " +
            "where b.id = ?1 " +
            "AND (u.id = ?2 " +
            "OR b.booker.id = ?2) ")
    Optional<Booking> findByBookingIdAndUserId(Long bookingId, Long userId);

    Page<Booking> findByBookerIdOrderByStartDesc(Long userId, Pageable page);

    List<Booking> findByBookerIdAndEndAfterAndStartBeforeOrderByStartDesc(Long userId, LocalDateTime currentDateStart, LocalDateTime currentDate);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime currentDate);

    List<Booking> findByBookerIdAndStartAfterAndEndAfterOrderByStartDesc(Long userId, LocalDateTime currentDateStart, LocalDateTime currentDateEnd);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long userId, Status status);

    Page<Booking> findByItemUserIdOrderByStartDesc(Long userId, Pageable page);

    List<Booking> findByItemUserIdAndEndAfterAndStartBeforeOrderByStartDesc(Long userId, LocalDateTime currentDateStart, LocalDateTime currentDate);

    List<Booking> findByItemUserIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime currentDate);

    List<Booking> findByItemUserIdAndStartAfterAndEndAfterOrderByStartDesc(Long userId, LocalDateTime currentDateStart, LocalDateTime currentDateEnd);

    List<Booking> findByItemUserIdAndStatusOrderByStartDesc(Long userId, Status status);

    List<Booking> findByItemId(Long itemId);

    List<Booking> findByItemIdAndBookerIdAndEndBefore(Long itemId, Long userId, LocalDateTime currentDate);
}
