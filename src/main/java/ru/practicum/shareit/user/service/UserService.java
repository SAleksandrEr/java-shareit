package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataConflictException;
import ru.practicum.shareit.user.dto.UserDtoPatch;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    private final UserMapper userMapper;

    @Autowired
    public UserService(@Qualifier("userDaoImpl") UserStorage userStorage, UserMapper userMapper) {
        this.userStorage = userStorage;
        this.userMapper = userMapper;
    }

   public UserResponse createUser(UserDtoRequest userDto) {
       User user = userMapper.toUser(userDto);
       validate(user);
       userStorage.createUser(user);
       return userMapper.toUserResponse(user);
    }

    public UserResponse updateUser(UserDtoPatch userDtoPatch) {
        User user = userMapper.toUserUpdate(userDtoPatch);
        validate(user);
        User oldUser = userStorage.getUsersId(user.getId());
        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            oldUser.setEmail(user.getEmail());
        }
        userStorage.updateUser(oldUser);
        log.info("Пользователь обновлён " + oldUser);
        return userMapper.toUserResponse(oldUser);
    }

    public UserResponse findUsersId(Long id) {
        User user = userStorage.getUsersId(id);
        log.info("Пользователь найден " + user);
        return userMapper.toUserResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userStorage.getAllUser();
        log.info("Найдены пользователи  " + users.size());
        return users.stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    public void deleteUserById(Long id) {
        userStorage.deleteUserById(id);
        log.info("Пользователь с id удален  " + id);
    }

    private void validate(User data) {
        if (userStorage.getAllUser().stream()
                .filter(user -> !Objects.equals(data.getId(), user.getId()))
                .anyMatch(user -> user.getEmail().equals(data.getEmail()))) {
            throw new DataConflictException("Invalid data" + data);
        }
    }
}

