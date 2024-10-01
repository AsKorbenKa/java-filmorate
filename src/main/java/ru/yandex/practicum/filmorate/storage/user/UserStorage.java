package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    User create(User user);

    User update(User newUser);

    void delete(User user);

    User getUserById(Long userId);

    Collection<User> findAll();

    Collection<User> findAllFriends(Long userId);

    Collection<User> findAllMutualFriends(Long userId, Long otherId);

    User addFriend(Long userId, Long friendId);

    User changeFriendshipStatus(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);
}
