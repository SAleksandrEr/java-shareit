package ru.practicum.shareit.user.storage;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    User createUser(User user);

    User updateUser(User user);

    List<User> getAllUser();

    User getUsersId(Long id);

    void deleteUserById(Long id);
}


