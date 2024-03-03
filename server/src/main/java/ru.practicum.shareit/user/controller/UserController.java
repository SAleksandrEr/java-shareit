package ru.practicum.shareit.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Transactional
    @PostMapping
    public UserResponse createUser(@RequestBody UserDtoRequest userRequest) {
        return userService.createUser(userRequest);
    }

    @Transactional
    @PatchMapping("/{id}")
    public UserResponse updateUser(@PathVariable("id") Long id, @RequestBody UserDtoRequest userPatch) {
        userPatch.setId(id);
        return userService.updateUser(userPatch);
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @Transactional
    @DeleteMapping("/{id}")
    public void userDeleteById(@PathVariable("id") Long id) {
        userService.deleteUserById(id);
    }

    @GetMapping("/{id}")
    public UserResponse findUsersId(@PathVariable("id") Long id) {
        return userService.findUsersId(id);
    }

}
