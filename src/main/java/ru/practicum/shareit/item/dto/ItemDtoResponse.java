package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(force = true)
public class ItemDtoResponse {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private BookingDtoResponse.BookingDtoResponseOwner lastBooking;

    private BookingDtoResponse.BookingDtoResponseOwner nextBooking;

    private List<CommentDtoResponse> comments;

    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor(force = true)
    public static class ItemDtoResponseBooking {

        private Long id;

        private String name;
    }
}