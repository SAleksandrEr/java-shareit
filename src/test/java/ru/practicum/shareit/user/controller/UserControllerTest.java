package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    private final UserResponse userResponse = UserResponse.builder().id(1L).name("test").email("test@test.ru").build();

    @Test
    void testCreateUser() throws Exception {
        when(userService.createUser(any())).thenReturn(userResponse);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userResponse.getName())))
                .andExpect(jsonPath("$.email", is(userResponse.getEmail())));
    }

    @Test
    void testUpdateUser() throws Exception {
        when(userService.updateUser(any())).thenReturn(userResponse);
        mvc.perform(patch("/users/{id}",1)
                        .content(mapper.writeValueAsString(userResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userResponse.getName())))
                .andExpect(jsonPath("$.email", is(userResponse.getEmail())));
    }

    @Test
    void testGetAllUsers() throws Exception {
        List<UserResponse> userResponseList = new ArrayList<>();
        userResponseList.add(userResponse);
        when(userService.getAllUsers()).thenReturn(userResponseList);
        mvc.perform(get("/users")
                        .content(mapper.writeValueAsString(userResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(userResponse.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(userResponse.getName())))
                .andExpect(jsonPath("$.[0].email", is(userResponse.getEmail())));
    }

    @Test
    void testUserDeleteById() throws Exception {
        userService.deleteUserById(anyLong());
        mvc.perform(delete("/users/{id}",1)
                        .content(mapper.writeValueAsString(userResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testFindUsersId() throws Exception {
        when(userService.findUsersId(anyLong())).thenReturn(userResponse);
        mvc.perform(get("/users/{id}", 1)
                        .content(mapper.writeValueAsString(userResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userResponse.getName())))
                .andExpect(jsonPath("$.email", is(userResponse.getEmail())));
    }
}