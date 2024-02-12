package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.user.dto.UserDtoPatch;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepositoryJpa;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepositoryJpa userStorage;

    @Mock
    UserMapper userMapper;

    UserDtoPatch userDtoPatch = UserDtoPatch.builder().id(1L).name("test1").email("test1@test.ru").build();

    @Test
    void testUpdateUser() {
        UserService userService = new UserService(userStorage, userMapper);
        User user = new User();
        user.setId(1L);
        user.setName("test1");
        user.setEmail("test1@test.ru");
        User oldUser = new User();
        oldUser.setId(1L);
        oldUser.setName("test");
        oldUser.setEmail("test@test.ru");
        Mockito.when(userMapper.toUserUpdate(any())).thenReturn(user);
        Mockito.when(userStorage.findById(anyLong())).thenReturn(Optional.of(oldUser));
        userStorage.updateUser(oldUser.getName(), oldUser.getEmail(), oldUser.getId());
        userService.updateUser(userDtoPatch);
        Assertions.assertEquals(oldUser.getName(), userDtoPatch.getName());
        Assertions.assertEquals(oldUser.getEmail(), userDtoPatch.getEmail());
    }

    @Test
    void testFindUsersId() {
        UserService userService = new UserService(userStorage, userMapper);
        User user = new User();
        user.setId(1L);
        user.setName("test1");
        user.setEmail("test1@test.ru");
        Mockito.when(userStorage.findById(anyLong())).thenThrow(new DataNotFoundException("User not found"));
        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class, () -> userService.findUsersId(user.getId()));

        Assertions.assertEquals("User not found", exception.getMessage());
    }
}