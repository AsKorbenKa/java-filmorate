package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserStorage userStorage;

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User newUser) {
        return userStorage.update(newUser);
    }

    public User getUserById(Long userId) {
        return userStorage.getUserById(userId);
    }

    public Collection<User> findAllFriends(Long userId) {
        return userStorage.findAllFriends(userId);
    }

    public Collection<User> findAllMutualFriends(Long userId, Long otherId) {
        return userStorage.findAllMutualFriends(userId, otherId);
    }

    // добавляем пользователей в друзья
    public User addFriend(Long userId, Long friendId) {
        return userStorage.addFriend(userId, friendId);
    }

    public User changeFriendshipStatus(Long userId, Long friendId) {
        return userStorage.changeFriendshipStatus(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        userStorage.removeFriend(userId, friendId);
    }

}
