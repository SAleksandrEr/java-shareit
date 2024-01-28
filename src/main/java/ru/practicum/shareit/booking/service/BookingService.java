package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
        Booking booking = bookingMapper.toBooking(bookingDto);
        User user = userRepositoryJpa.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        Item item = itemRepositoryJpa.findById(bookingDto.getItemId())
                .orElseThrow(() -> new DataNotFoundException("Item not found"));
        if (Objects.equals(item.getUser().getId(), userId)) {
            throw new DataNotFoundException("The owner cannot accept the Booking " + userId);
        }
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        validate(booking);
        return bookingMapper.toBookingResponse(bookingRepositoryJpa.save(booking));
    }

    @Transactional
    public BookingDtoResponse updateStatusBooking(Long userId, Long bookingId, String approved) {
        userRepositoryJpa.findById(userId).orElseThrow(() -> new DataNotFoundException("User not found"));
        Status status = null;
        if (Objects.equals(approved, "true")) {
            status = Status.APPROVED;
        }
        if (Objects.equals(approved, "false")) {
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

    public List<BookingDtoResponse> getBookingByCurrentUser(Long userId, State state) {
        userRepositoryJpa.findById(userId).orElseThrow(() -> new DataNotFoundException("User not found"));
            List<Booking> bookingList;
            LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        switch (state) {
            case ALL:
                bookingList = bookingRepositoryJpa.findByBookerIdOrderByStartDesc(userId);
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

    public List<BookingDtoResponse> getBookingByOwner(Long userId, State state) {
       userRepositoryJpa.findById(userId).orElseThrow(() -> new DataNotFoundException("User not found"));
       List<Item> itemsList = itemRepositoryJpa.findItemsByUserId(userId);
       if (itemsList.size() > 0) {
           List<Booking> bookingList;
           LocalDateTime currentDate = LocalDateTime.now().withNano(0);;
           switch (state) {
               case ALL:
                   bookingList = bookingRepositoryJpa.findByItemUserIdOrderByStartDesc(userId);
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
       } else {
           throw new ValidationException("Invalid data " + itemsList);
       }
    }

        private void validate(Booking booking) {
            LocalDateTime startDate = booking.getStart();
            LocalDateTime endDate = booking.getEnd();
            LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        if (startDate.isBefore(endDate) && startDate.isAfter(currentDate)) {
            List<Booking> bookingList = bookingRepositoryJpa.findByBookingStartBeforeAndEndBefore(startDate, endDate, Status.REJECTED, booking.getItem().getId());
            Item item = itemRepositoryJpa.findByIdAndAvailableTrue(booking.getItem().getId());
            if ((bookingList.size() > 0) || (item == null)) {
                throw new ValidationException("Invalid data " + booking.getStart() + " " + booking.getEnd());
            }
        } else {
            throw new ValidationException("Invalid data " + booking.getStart() + " " + booking.getEnd());
        }
    }
}
