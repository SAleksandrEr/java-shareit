package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataConflictException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDaoImpl") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

   public User createUser(User user) {
       validate(user);
       log.info("Пользователь создан " + user);
       return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        validate(user);
        User newUser = userStorage.getUsersId(user.getId());
        if (user.getName() != null) {
        newUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            newUser.setEmail(user.getEmail());
        }
        log.info("Пользователь обновлён " + newUser);
        return userStorage.updateUser(newUser);
    }

    public User findUsersId(Long id) {
        User user = userStorage.getUsersId(id);
        log.info("Пользователь найден " + user);
        return user;
    }

    public List<User> getAllUsers() {
        List<User> users = userStorage.getAllUser();
        log.info("Найдены пользователи  " + users.size());
        return users;
    }

    public void deleteUserById(Long id) {
        userStorage.deleteUserById(id);
        log.info("Пользователь с id удален  " + id);
    }

    private void validate(User data) {
        if (userStorage.getAllUser().stream().filter(user -> !Objects.equals(data.getId(), user.getId()))
                .anyMatch(user -> user.getEmail().equals(data.getEmail()))) {
            throw new DataConflictException("Invalid date" + data);
        }
    }
}

