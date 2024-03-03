package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingRepositoryJpa;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepositoryJpa;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepositoryJpa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
public class BookingService {

    private final BookingMapper bookingMapper;

    private final BookingRepositoryJpa bookingRepositoryJpa;

    private final UserRepositoryJpa userRepositoryJpa;

    private final ItemRepositoryJpa itemRepositoryJpa;

    @Transactional
    public BookingDtoResponse createBooking(Long userId, BookingDto bookingDto) {
        User user = userRepositoryJpa.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        Item item = itemRepositoryJpa.findById(bookingDto.getItemId())
                .orElseThrow(() -> new DataNotFoundException("Item not found"));
        if (Objects.equals(item.getUser().getId(), userId)) {
            throw new DataNotFoundException("The owner cannot accept the Booking " + userId);
        }
        validate(bookingDto);
        Booking booking = bookingMapper.toBooking(bookingDto);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        return bookingMapper.toBookingResponse(bookingRepositoryJpa.save(booking));
    }

    @Transactional
    public BookingDtoResponse updateStatusBooking(Long userId, Long bookingId, Boolean approved) {
        userRepositoryJpa.findById(userId).orElseThrow(() -> new DataNotFoundException("User not found"));
        Status status = null;
        if (Objects.equals(approved, true)) {
            status = Status.APPROVED;
        }
        if (Objects.equals(approved, false)) {
            status = Status.REJECTED;
        }
        Booking booking = bookingRepositoryJpa.findByBookingIdAndOwner(bookingId, userId)
                .orElseThrow(() -> new DataNotFoundException("User is not owner"));
        if (bookingRepositoryJpa.updateBooking(status, bookingId) == 0) {
            throw new ValidationException("Booking status is APPROVED ");
        }
        booking.setStatus(status);
        return bookingMapper.toBookingResponse(booking);
    }

    public BookingDtoResponse getBookingIdByAuthorOrOwner(Long userId, Long bookingId) {
        userRepositoryJpa.findById(userId).orElseThrow(() -> new DataNotFoundException("User not found"));
        return bookingMapper.toBookingResponse(bookingRepositoryJpa.findByBookingIdAndUserId(bookingId, userId)
                .orElseThrow(() -> new DataNotFoundException("User is not owner or author")));
    }

    public List<BookingDtoResponse> getBookingByCurrentUser(Long userId, State state, Pageable page) {
        userRepositoryJpa.findById(userId).orElseThrow(() -> new DataNotFoundException("User not found"));
            List<Booking> bookingList;
            LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        switch (state) {
            case ALL:
                bookingList = bookingRepositoryJpa.findByBookerIdOrderByStartDesc(userId,page).getContent();
                break;
            case CURRENT:
                bookingList = bookingRepositoryJpa.findByBookerIdAndEndAfterAndStartBeforeOrderByStartDesc(userId, currentDate, currentDate);
                break;
            case PAST:
                bookingList = bookingRepositoryJpa.findByBookerIdAndEndBeforeOrderByStartDesc(userId, currentDate);
                break;
            case FUTURE:
                bookingList = bookingRepositoryJpa.findByBookerIdAndStartAfterAndEndAfterOrderByStartDesc(userId, currentDate, currentDate);
                break;
            case WAITING:
                bookingList = bookingRepositoryJpa.findByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
                break;
            case REJECTED:
                bookingList = bookingRepositoryJpa.findByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
                break;
            case UNSUPPORTED_STATUS:
            default:
                throw new ValidationException("Unknown state: " + state);
        }
        return bookingList.stream().map(bookingMapper::toBookingResponse).collect(Collectors.toList());
    }

    public List<BookingDtoResponse> getBookingByOwner(Long userId, State state, Pageable page) {
       userRepositoryJpa.findById(userId).orElseThrow(() -> new DataNotFoundException("User not found"));
           List<Booking> bookingList;
           LocalDateTime currentDate = LocalDateTime.now().withNano(0);;
           switch (state) {
               case ALL:
                   bookingList = bookingRepositoryJpa.findByItemUserIdOrderByStartDesc(userId, page).getContent();
                   break;
               case CURRENT:
                   bookingList = bookingRepositoryJpa.findByItemUserIdAndEndAfterAndStartBeforeOrderByStartDesc(userId, currentDate, currentDate);
                   break;
               case PAST:
                   bookingList = bookingRepositoryJpa.findByItemUserIdAndEndBeforeOrderByStartDesc(userId, currentDate);
                   break;
               case FUTURE:
                   bookingList = bookingRepositoryJpa.findByItemUserIdAndStartAfterAndEndAfterOrderByStartDesc(userId, currentDate, currentDate);
                   break;
               case WAITING:
                   bookingList = bookingRepositoryJpa.findByItemUserIdAndStatusOrderByStartDesc(userId, Status.WAITING);
                   break;
               case REJECTED:
                   bookingList = bookingRepositoryJpa.findByItemUserIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
                   break;
               case UNSUPPORTED_STATUS:
               default:
                   throw new ValidationException("Unknown state: " + state);
           }
          return bookingList.stream().map(bookingMapper::toBookingResponse).collect(Collectors.toList());
    }

        private void validate(BookingDto booking) {
            LocalDateTime startDate = LocalDateTime.parse(booking.getStart());
            LocalDateTime endDate = LocalDateTime.parse(booking.getEnd());
            List<Booking> bookingList = bookingRepositoryJpa.findByBookingStartBeforeAndEndBefore(startDate, endDate, Status.REJECTED, booking.getItemId());
            Item item = itemRepositoryJpa.findByIdAndAvailableTrue(booking.getItemId());
            if ((bookingList.size() > 0) || (item == null)) {
                throw new ValidationException("Invalid data " + booking.getStart() + " " + booking.getEnd());
            }
    }
}
