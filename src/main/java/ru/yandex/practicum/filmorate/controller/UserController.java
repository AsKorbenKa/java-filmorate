package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.RecommendationService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;
    RecommendationService recommendationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        return userService.update(newUser);
    }

    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") Long id) {
        return userService.getUserById(id);
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

    @GetMapping("{id}/recommendations")
    public List<FilmDto> findRecommendedFilms(@PathVariable("id") Long userId) {
        return recommendationService.getRecommendedFilms(userId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable("id") Long id,
                          @PathVariable("friendId") Long friendId) {
        return userService.addFriend(id, friendId);
    }

    @PutMapping("/{id}/friends/{friendId}/status")
    public User changeFriendshipStatus(@PathVariable("id") Long id,
                                       @PathVariable("friendId") Long friendId) {
        return userService.changeFriendshipStatus(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFriend(@PathVariable("id") Long id,
                             @PathVariable("friendId") Long friendId) {
        userService.removeFriend(id, friendId);
    }

    @DeleteMapping("/{userId}")
    public User delete(@PathVariable("userId") Long id) {
        return userService.delete(id);
    }
}
