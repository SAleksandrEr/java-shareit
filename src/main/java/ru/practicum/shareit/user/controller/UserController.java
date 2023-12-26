package ru.practicum.shareit.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDtoPatch;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserResponse createUser(@Valid @RequestBody UserDtoRequest userRequest) {
        return userService.createUser(userRequest);
    }

    @PatchMapping("/{id}")
    public UserResponse updateUser(@PathVariable("id") Long id, @RequestBody UserDtoPatch userPatch) {
        userPatch.setId(id);
        return userService.updateUser(userPatch);
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/{id}")
    public void userDeleteById(@PathVariable("id") Long id) {
        userService.deleteUserById(id);
    }

    @GetMapping("/{id}")
    public UserResponse findUsersId(@PathVariable("id") Long id) {
        return userService.findUsersId(id);
    }

}
