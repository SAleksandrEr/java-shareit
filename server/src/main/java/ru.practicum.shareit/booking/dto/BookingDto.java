package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(force = true)
public class BookingDto {

    private Long id;

    private String start;

    private String end;

    private Long itemId;

    private Long bookerId;
}
