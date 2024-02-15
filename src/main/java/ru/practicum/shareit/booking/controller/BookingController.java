package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.GetBookingParam;
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
            return bookingService.createBooking(userId, bookingDto);
    }

    @Transactional
    @PatchMapping("/{bookingId}")
    public BookingDtoResponse updateBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long bookingId,
                                             @RequestParam(value = "approved") Boolean approved) {
        if ((userId != null) && (bookingId > 0)) {
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
                                                            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
                                                            @RequestParam(defaultValue = "0", required = false) int from,
                                                            @RequestParam(defaultValue = "10", required = false) int size) {
        if (from < 0 || size < 1) {
            throw new ValidationException("Param - <from> or <size> is not correct");
        }

        return bookingService.getBookingByCurrentUser(userId, State.valueOf(state), GetBookingParam.pageRequest(from, size));
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getBookingByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
                                                      @RequestParam(defaultValue = "0", required = false) int from,
                                                      @RequestParam(defaultValue = "10", required = false) int size) {
        if (from < 0 || size < 1) {
            throw new ValidationException("Param - <from> or <size> is not correct");
        }
        return bookingService.getBookingByOwner(userId, State.valueOf(state), GetBookingParam.pageRequest(from, size));
    }
}
