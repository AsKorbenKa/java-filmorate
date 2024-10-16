package ru.yandex.practicum.filmorate.storage.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.enums.Operation;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;

import java.util.Collection;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserDbStorage extends BaseStorage<User> implements UserStorage {
    static Logger log = LoggerFactory.getLogger(UserDbStorage.class.getName());
    static String FIND_ALL_QUERY = "SELECT * FROM users";
    static String CREATE_USER_QUERY = "INSERT INTO users (name, login, email, birthday) " +
            "VALUES (?, ?, ?, ?)";
    static String DELETE_USER_QUERY = "DELETE FROM users WHERE user_id = ?";
    static String GET_USER_QUERY = "SELECT * FROM users WHERE user_id = ?";
    static String UPDATE_USER_QUERY = "UPDATE users SET name = ?, login = ?, email = ?, birthday = ?" +
            " WHERE user_id = ?";
    static String FIND_ALL_FRIENDS_QUERY = "SELECT * FROM users WHERE user_id in " +
            "(SELECT friend_id FROM friendship WHERE user_id = ? AND status = 'Confirmed')";
    static String FIND_ALL_MUTUAL_FRIENDS_QUERY = "SELECT * FROM users AS u WHERE u.user_id IN " +
            "(SELECT f2.friend_id FROM friendship AS f2 WHERE f2.user_id = ? AND f2.status = 'Confirmed'\n" +
            "INTERSECT \n" +
            "SELECT friend_id FROM friendship AS f WHERE f.user_id = ? AND f.status = 'Confirmed');";
    static String ADD_FRIEND_QUERY = "INSERT INTO friendship (user_id, friend_id, status) " +
            "VALUES (?, ?, 'Confirmed')";
    static String CHANGE_FRIENDSHIP_STATUS_QUERY = "UPDATE friendship SET status = 'Confirmed'" +
            " WHERE user_id = ? AND friend_id = ?";
    static String REMOVE_FRIEND_QUERY = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";

    FeedStorage feedStorage;

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper, FeedStorage feedStorage) {
        super(jdbc, mapper);
        this.feedStorage = feedStorage;
    }

    @Override
    public Collection<User> findAll() {
        log.debug("Получаем список всех пользователей.");
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public User create(User user) {
        log.debug("Создаем нового пользователя.");
        long key;

        if (user.getName() == null || user.getName().isBlank()) {
            key = insert(CREATE_USER_QUERY, user.getLogin(), user.getLogin(), user.getEmail(),
                    user.getBirthday().toString());
        } else {
            key = insert(CREATE_USER_QUERY, user.getName(), user.getLogin(), user.getEmail(),
                    user.getBirthday().toString());
        }
        return getUserById(key);
    }

    @Override
    public User update(User newUser) {
        log.debug("Обновляем данные пользователя.");
        // проверяем есть ли пользователь в бд
        getUserById(newUser.getId());
        update(UPDATE_USER_QUERY, newUser.getName(), newUser.getLogin(), newUser.getEmail(),
                newUser.getBirthday(), newUser.getId());
        return getUserById(newUser.getId());
    }

    @Override
    public void delete(Long userId) {
        log.debug("Удаляем пользователя.");
        update(DELETE_USER_QUERY, userId);
    }

    @Override
    public User getUserById(Long userId) {
        log.debug("Получаем пользователя по его id.");
        return findOne(GET_USER_QUERY, userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id " + userId + " не найден."));
    }

    @Override
    public Collection<User> findAllFriends(Long userId) {
        log.debug("Получаем всех друзей пользователя.");
        // проверяем есть ли пользователь в бд
        getUserById(userId);
        return findMany(FIND_ALL_FRIENDS_QUERY, userId);
    }

    @Override
    public Collection<User> findAllMutualFriends(Long userId, Long otherId) {
        log.debug("Получаем общих друзей двух пользователей.");
        // проверяем есть ли пользователь в бд
        getUserById(userId);
        getUserById(otherId);
        return findMany(FIND_ALL_MUTUAL_FRIENDS_QUERY, userId, otherId);
    }

    @Override
    public User addFriend(Long userId, Long friendId) {
        log.debug("Добавляем нового друга.");
        // проверяем есть ли пользователь в бд
        getUserById(userId);
        getUserById(friendId);
        update(ADD_FRIEND_QUERY, userId, friendId);
        //добавление в ленту событий
        feedStorage.addFeed(friendId, userId, EventType.FRIEND, Operation.ADD);
        return getUserById(userId);
    }

    @Override
    public User changeFriendshipStatus(Long userId, Long friendId) {
        log.debug("Меняем статус дружбы пользователей.");
        // проверяем есть ли пользователь в бд
        getUserById(userId);
        getUserById(friendId);
        update(CHANGE_FRIENDSHIP_STATUS_QUERY, userId, friendId);
        return getUserById(userId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        log.debug("Удаляем пользователей из друзей.");
        // проверяем есть ли пользователь в бд
        getUserById(userId);
        getUserById(friendId);
        update(REMOVE_FRIEND_QUERY, userId, friendId);
        //добавление в ленту событий
        feedStorage.addFeed(friendId, userId, EventType.FRIEND, Operation.REMOVE);
    }
}
