package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(force = true)
public class CommentDtoResponse {

    private Long id;

    private String text;

    private String authorName;

    private String created;
}
