package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.user.dto.UserResponse;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(force = true)
public class BookingDtoResponse {

    private Long id;

    private String start;

    private String end;

    private Status status;

    private UserResponse.UserResponseBooking booker;

    private ItemDtoResponse.ItemDtoResponseBooking item;


    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor(force = true)
    public static class BookingDtoResponseOwner {

        private Long id;

        private Long bookerId;

    }
}
