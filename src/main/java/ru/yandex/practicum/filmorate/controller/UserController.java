package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserStorage inMemoryUserStorage;
    private final UserService userService;

    @GetMapping
    public Collection<User> findAll() {
        return inMemoryUserStorage.findAll();
    }

    @GetMapping("/{id}/friends")
    public Collection<User> findAllFriends(@PathVariable("id") Long id) {
        return userService.findAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> findAllMutualFriends(@PathVariable("id") Long id,
                                           @PathVariable("otherId") Long otherId) {
        return userService.findAllMutualFriends(id, otherId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User user) {
        return inMemoryUserStorage.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        return inMemoryUserStorage.update(newUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable("id") Long id,
                          @PathVariable("friendId") Long friendId) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFriend(@PathVariable("id") Long id,
                             @PathVariable("friendId") Long friendId) {
        userService.removeFriend(id, friendId);
    }
}
