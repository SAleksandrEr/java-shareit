package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepositoryJpa;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
public class UserService {

    private final UserRepositoryJpa userStorage;

    private final UserMapper userMapper;

   @Transactional
   public UserResponse createUser(UserDtoRequest userDto) {
       User user = userMapper.toUser(userDto);
       userStorage.save(user);
       return userMapper.toUserResponse(user);
    }

    @Transactional
    public UserResponse updateUser(UserDtoRequest userDtoPatch) {
        User user = userMapper.toUser(userDtoPatch);
        User oldUser = userStorage.findById(user.getId())
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            oldUser.setEmail(user.getEmail());
        }
        userStorage.updateUser(oldUser.getName(), oldUser.getEmail(), oldUser.getId());
        log.info("Пользователь обновлён " + oldUser);
        return userMapper.toUserResponse(oldUser);
    }

    public UserResponse findUsersId(Long id) {
        User user = userStorage.findById(id).orElseThrow(() -> new DataNotFoundException("User not found"));;
        log.info("Пользователь найден " + user);
        return userMapper.toUserResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userStorage.findAll();
        log.info("Найдены пользователи  " + users.size());
        return users.stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteUserById(Long id) {
        userStorage.deleteById(id);
        log.info("Пользователь с id удален  " + id);
    }
}

