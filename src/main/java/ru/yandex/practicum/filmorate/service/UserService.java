package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage inMemoryUserStorage;
    private static final Logger log = LoggerFactory.getLogger(UserService.class.getName());

    public List<User> findAllFriends(Long userId) {
        log.info("Составляем список друзей пользователя.");

        // проверяем, что пользователь существует
        Optional<User> userToCheck = inMemoryUserStorage.findAll().stream()
                .filter(userInMap -> Objects.equals(userInMap.getId(), userId))
                .findFirst();

        if (userToCheck.isEmpty()) {
            log.error("Ошибка при составлении списка друзей. Пользователь с id " + userId + " не найден.");
            throw new UserNotFoundException("Ошибка при составлении списка друзей. " +
                    "Пользователь с id " + userId + " не найден.");
        } else {
            User user = userToCheck.get();
            log.trace("Список друзей пользователя успешно составлен.");
            return inMemoryUserStorage.findAll().stream()
                    .filter(userInMap -> user.getFriends().contains(userInMap.getId()))
                    .toList();
        }
    }

    public List<User> findAllMutualFriends(Long userId, Long otherId) {
        log.info("Составляем список общих друзей.");
        List<User> users = checkUsersExist(userId, otherId, "поиске общих друзей");
        List<Long> mutualFriends = users.getFirst().getFriends().stream()
                .filter(friendInSet -> users.get(1).getFriends().contains(friendInSet))
                .toList();
        return inMemoryUserStorage.findAll().stream()
                .filter(userInMap -> mutualFriends.contains(userInMap.getId()))
                .toList();
    }

    // добавляем пользователей в друзья
    public User addFriend(Long userId, Long friendId) {
        log.info("Добавляем пользователей в друзья.");
        List<User> users = checkUsersExist(userId, friendId, "добавлении в друзья");
        users.getFirst().getFriends().add(friendId);
        users.get(1).getFriends().add(userId);
        log.trace("Пользователи успешно добавлены друг другу в друзья");
        return users.getFirst();
    }

    public void removeFriend(Long userId, Long friendId) {
        log.info("Удаляем пользователей из друзей.");
        List<User> users = checkUsersExist(userId, friendId, "удалении из друзей");
        // проверяем наличие одного пользователя на наличие в списке друзей другого
        // поскольку добавление в друзья происходить взаимно, достаточно одной проверки на наличие
        if (users.getFirst().getFriends().contains(friendId)) {
            users.getFirst().getFriends().remove(friendId);
            users.get(1).getFriends().remove(userId);
        } else {
            log.error("Пользователи с id " + userId + " и " + friendId + " не являются друзьями");
            throw new ConditionsNotMetException("Пользователи с id " + userId + " и " + friendId + " не являются друзьями");
        }
        log.trace("Пользователи успешно удалены из друзей друг друга");
    }

    private List<User> checkUsersExist(Long userId, Long friendId, String message) {
        log.info("Проверяем данные пользователей на наличие в нашем списке пользователей.");
        if (userId.equals(friendId)) {
            log.error("Ошибка при " + message + ". " +
                    "Пользователь не может добавлять в друзья самого себя.");
            throw new ConditionsNotMetException("Ошибка при " + message + ". " +
                    "Пользователь не может добавлять в друзья самого себя.");
        }

        Optional<User> userToCheck = inMemoryUserStorage.findAll().stream()
                .filter(userInMap -> Objects.equals(userInMap.getId(), userId))
                .findFirst();

        Optional<User> friendToCheck = inMemoryUserStorage.findAll().stream()
                .filter(userInMap -> Objects.equals(userInMap.getId(), friendId))
                .findFirst();

        if (userToCheck.isEmpty() && friendToCheck.isEmpty()) {
            log.error("Ошибка при " + message + ". " +
                    "Пользователи с id " + userId + " и " + friendId + " не найдены.");
            throw new UserNotFoundException("Ошибка при " + message + ". " +
                    "Пользователи с id " + userId + " и " + friendId + " не найдены.");
        }
        if (userToCheck.isEmpty()) {
            log.error("Ошибка при " + message + ". Пользователь с id " + userId + " не найден.");
            throw new UserNotFoundException("Ошибка при " + message + ". Пользователь с id " + userId + " не найден.");
        }
        if (friendToCheck.isEmpty()) {
            log.error("Ошибка при " + message + ". Пользователь с id " + friendId + " не найден.");
            throw new UserNotFoundException("Ошибка при " + message + ". Пользователь с id " + friendId + " не найден.");
        }
        log.trace("Данные пользователей были успешно найдены");
        return new ArrayList<>(List.of(userToCheck.get(), friendToCheck.get()));
    }
}
