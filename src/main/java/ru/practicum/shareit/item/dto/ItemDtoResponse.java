package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(force = true)
public class ItemDtoResponse {

    private Long id;

    private String name;

    private String description;

    private Boolean available;
}
