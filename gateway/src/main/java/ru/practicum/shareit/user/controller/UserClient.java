package ru.practicum.shareit.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDtoPatch;
import ru.practicum.shareit.user.dto.UserDtoRequest;

@Service
public class UserClient extends BaseClient {

    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> createUser(UserDtoRequest userRequest) {
        return post("", userRequest);
    }

    public ResponseEntity<Object> updateUser(UserDtoPatch userPatch) {
        return patch("/" + userPatch.getId(), userPatch);
    }

    public ResponseEntity<Object> getAllUsers() {
        return get("");
    }

    public void userDeleteById(Long id) {
        delete("/" + id);
    }

    public ResponseEntity<Object> findUsersId(Long id) {
        return get("/" + id);
    }
}
