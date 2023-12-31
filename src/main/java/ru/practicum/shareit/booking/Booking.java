package ru.practicum.shareit.booking;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Getter
@Setter
public class Booking {

    private Long id;

    private LocalDateTime strart;

    private LocalDateTime end;

    private Item item;

    private User booker;

    private Status status;

}

