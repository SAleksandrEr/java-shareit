package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(force = true)
public class CommentDto {

    private Long id;

    @NotBlank
    private String text;

    private Long itemId;

    private Long authorId;
}
