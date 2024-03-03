package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDtoPatch;
import ru.practicum.shareit.user.dto.UserDtoRequest;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserClient userClient;


    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDtoRequest userRequest) {
        return userClient.createUser(userRequest);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable("id") Long id, @RequestBody UserDtoPatch userPatch) {
        userPatch.setId(id);
        return userClient.updateUser(userPatch);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return userClient.getAllUsers();
    }

    @DeleteMapping("/{id}")
    public void userDeleteById(@PathVariable("id") Long id) {
        userClient.userDeleteById(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findUsersId(@PathVariable("id") Long id) {
        return userClient.findUsersId(id);
    }
}
