package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(UserController.class.getName());

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Создаем запись о пользователе");
        // формируем дополнительные данные
        user.setId(getNextId());
        boolean isEmailExist = users.values().stream()
                .anyMatch(userStream -> userStream.getEmail().equals(user.getEmail()));
        // если email уже используется, то выбрасываем ошибку
        if (isEmailExist) {
            log.error("Этот имейл уже используется");
            throw new ValidationException("Этот имейл уже используется");
        }
        // если у пользователя не указано имя, вместо имени используем логин
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        // сохраняем нового пользователя в памяти приложения
        users.put(user.getId(), user);
        log.trace("Запись о пользователе успешно создана");
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        log.info("Обновляем запись о пользователе");
        boolean isEmailExist = users.values().stream()
                .anyMatch(userStream -> userStream.getEmail().equals(newUser.getEmail()));

        if (newUser.getId() == null) {
            log.error("Id должен быть указан");
            throw new ValidationException("Id должен быть указан");
        }

        // если email уже используется, то выбрасываем ошибку
        if (isEmailExist) {
            log.error("Этот имейл уже используется");
            throw new ValidationException("Этот имейл уже используется");
        }

        // если у пользователя не указано имя, вместо имени используем логин
        if (newUser.getName() == null) {
            newUser.setName(newUser.getLogin());
        }

        if (users.containsKey(newUser.getId())) {
            // если пользователь найден и все условия соблюдены, обновляем содержимое
            users.put(newUser.getId(), newUser);
        } else {
            log.error("Пользователь со следующим id не найден: " + newUser.getId());
            throw new ValidationException("Пользователь со следующим id не найден: " + newUser.getId());
        }
        log.trace("Данные о пользователе успешно обновлены");
        return newUser;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
