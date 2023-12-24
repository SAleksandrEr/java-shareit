package ru.practicum.shareit.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(force = true)
public class UserDtoRequest {

    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;
}

