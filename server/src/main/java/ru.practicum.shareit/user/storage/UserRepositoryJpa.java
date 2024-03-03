package ru.practicum.shareit.user.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.user.model.User;


public interface UserRepositoryJpa extends JpaRepository<User, Long> {

    @Modifying
    @Query("UPDATE User " +
            "SET name = ?1, " +
            "email = ?2 " +
            "where id = ?3")
    void updateUser(String name, String email, Long userId);
}
