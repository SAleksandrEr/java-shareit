package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ValidationException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @Transactional
    @PostMapping
    public BookingDtoResponse createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @Valid @RequestBody BookingDto bookingDto) {
        if (userId != null) {
            return bookingService.createBooking(userId, bookingDto);
        } else {
            throw new ValidationException("Invalid data - userId");
        }
    }

    @Transactional
    @PatchMapping("/{bookingId}")
    public BookingDtoResponse updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long bookingId,
                                             @RequestParam(value = "approved") Boolean approved) {
        if ((userId != null) && (bookingId != null)) {
            return bookingService.updateStatusBooking(userId, bookingId, approved);
        } else {
            throw new ValidationException("Invalid data");
        }
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBookingIdByAuthorOrOwner(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("bookingId") Long bookingId) {
        return bookingService.getBookingIdByAuthorOrOwner(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoResponse> getBookingByCurrentUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state) {
        return bookingService.getBookingByCurrentUser(userId, State.valueOf(state));
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getBookingByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state) {
        return bookingService.getBookingByOwner(userId, State.valueOf(state));
    }
}
