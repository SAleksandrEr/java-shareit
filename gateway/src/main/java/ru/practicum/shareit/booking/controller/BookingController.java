package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.ValidationException;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @Valid @RequestBody BookingDto bookingDto) {
        LocalDateTime startDate = LocalDateTime.parse(bookingDto.getStart());
        LocalDateTime endDate = LocalDateTime.parse(bookingDto.getEnd());
        LocalDateTime currentDate = LocalDateTime.now().withNano(0);
        if (startDate.isAfter(endDate) || startDate.isBefore(currentDate) || startDate.equals(endDate)) {
            throw new ValidationException("Invalid data " + bookingDto.getStart() + " " + bookingDto.getEnd());
        }
            return bookingClient.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long bookingId,
                                             @RequestParam(value = "approved") Boolean approved) {
        if ((userId != null) && (bookingId > 0)) {
            return bookingClient.updateBooking(userId, bookingId, approved);
        } else {
            throw new ValidationException("Invalid data");
        }
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingIdByAuthorOrOwner(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("bookingId") Long bookingId) {
        return bookingClient.getBookingIdByAuthorOrOwner(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingByCurrentUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                            @RequestParam(value = "state", required = false, defaultValue = "ALL") String stateParam,
                                                            @RequestParam(defaultValue = "0", required = false) int from,
                                                            @RequestParam(defaultValue = "10", required = false) int size) {
        if (from < 0 || size < 1) {
            throw new ValidationException("Param - <from> or <size> is not correct");
        }
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new ValidationException("Unknown state: " + stateParam));
        return bookingClient.getBookingByCurrentUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestParam(value = "state", required = false, defaultValue = "ALL") String stateParam,
                                                      @RequestParam(defaultValue = "0", required = false) int from,
                                                      @RequestParam(defaultValue = "10", required = false) int size) {
        if (from < 0 || size < 1) {
            throw new ValidationException("Param - <from> or <size> is not correct");
        }
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new ValidationException("Unknown state: " + stateParam));
        return bookingClient.getBookingByOwner(userId, state, from, size);
    }
}
