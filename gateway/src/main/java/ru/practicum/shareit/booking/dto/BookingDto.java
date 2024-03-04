package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(force = true)
public class BookingDto {

    private Long id;

    @NotBlank
    private String start;

    @NotBlank
    private String end;

    @NonNull
    private Long itemId;

    @NonNull
    private Long bookerId;
}
