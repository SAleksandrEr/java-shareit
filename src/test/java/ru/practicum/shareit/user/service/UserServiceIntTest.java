package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntTest {

    private final UserService userService;

    @Test
    void testGetAllUsers() {
        UserDtoRequest userDto = UserDtoRequest.builder().name("test1").email("test1@test.ru").build();
        UserResponse userResponse = userService.createUser(userDto);
        List<UserResponse> userResponseList = userService.getAllUsers();
        assertThat(userResponseList.size(), notNullValue());
        assertThat(userResponseList.get(0).getId(), equalTo(userResponse.getId()));
        assertThat(userResponseList.get(0).getName(), equalTo(userResponse.getName()));
        assertThat(userResponseList.get(0).getEmail(), equalTo(userResponse.getEmail()));
    }

    @Test
    void testFindUsersId() {
        UserDtoRequest userDto = UserDtoRequest.builder().name("test1").email("test1@test.ru").build();
        UserResponse userResponse = userService.createUser(userDto);
        UserResponse userResponseNew = userService.findUsersId(userResponse.getId());
        assertThat(userResponseNew.getId(), equalTo(userResponse.getId()));
        assertThat(userResponseNew.getName(), equalTo(userResponse.getName()));
        assertThat(userResponseNew.getEmail(), equalTo(userResponse.getEmail()));
    }

    @Test
    void testDeleteUserById() {
        UserDtoRequest userDto = UserDtoRequest.builder().name("test1").email("test1@test.ru").build();
        UserResponse userResponse = userService.createUser(userDto);
        userService.deleteUserById(userResponse.getId());
        final DataNotFoundException exception = Assertions.assertThrows(
                DataNotFoundException.class, () -> userService.findUsersId(userResponse.getId()));

        Assertions.assertEquals("User not found", exception.getMessage());
    }
}
