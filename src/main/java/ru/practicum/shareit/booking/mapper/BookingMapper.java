package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    Booking toBooking(BookingDto bookingDto);

    BookingDtoResponse toBookingResponse(Booking booking);

    @Mapping(target = "bookerId", source = "booking.booker.id")
    BookingDtoResponse.BookingDtoResponseOwner toBookingResponseOwner(Booking booking);
}
