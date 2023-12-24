package ru.practicum.shareit.user.storage.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("userDaoImpl")
public class UserDaoImpl implements UserStorage {
    private Long generationId = 0L;
    private final Map<Long, User> userList = new HashMap<>();


    @Override
    public User createUser(User user) {
        user.setId(getGenerationId());
        userList.put(user.getId(),user);
        return userList.get(user.getId());
    }

    @Override
    public User updateUser(User user) {
        userList.put(user.getId(),user);
        return userList.get(user.getId());
    }

    @Override
    public List<User> getAllUser() {
        return new ArrayList<>(userList.values());
    }

    @Override
    public User getUsersId(Long id) {
        User user = userList.get(id);
        if (user == null) {
            throw new DataNotFoundException("Data not found " + id);
        }
        return user;
    }

    @Override
    public void deleteUserById(Long id) {
        userList.remove(id);
    }

    private Long getGenerationId() {
        return ++generationId;
    }
}
