package ru.practicum.shareit.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDtoPatch;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping
    public UserResponse createUser(@Valid @RequestBody UserDtoRequest userRequest) {
        User user = userMapper.toUser(userRequest);
        User modifiUser = userService.createUser(user);
        return userMapper.toUserResponse(modifiUser);
    }

    @PatchMapping("/{id}")
    public UserResponse updateUser(@PathVariable("id") Long id, @RequestBody UserDtoPatch userPatch) {
        if (id != null & id > 0) {
            userPatch.setId(id);
        } else {
            throw new ValidationException("Invalid date" + userPatch);
        }
        User user = userMapper.toUserUpdate(userPatch);
        User modifiUser = userService.updateUser(user);
        return userMapper.toUserResponse(modifiUser);
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{id}") //удаление пользователя по id
    public void userDeleteById(@PathVariable("id") Long id) {
        userService.deleteUserById(id);
    }

    @GetMapping("/{id}")
    public UserResponse findUsersId(@PathVariable("id") Long id) {
        User user = userService.findUsersId(id);
        return userMapper.toUserResponse(user);
    }

}
