package ru.practicum.shareit.user.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor(force = true)
public class User {

    private Long id;

    private String name;

    private String email;
}
