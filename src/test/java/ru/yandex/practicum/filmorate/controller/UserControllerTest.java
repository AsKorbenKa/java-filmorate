package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
    private User user;
    private static Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        user = new User();
    }

    @Test
    void validateUserEmail() {
        user.setId(1L);
        user.setEmail("korben.sweetheart");
        user.setLogin("korben.sweetheart");
        user.setName("Корбен Даллас");
        user.setBirthday(LocalDate.of(1955, 3, 19));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Электронная почта должна содержать символ @", violations.iterator().next().getMessage());
    }

    @Test
    void validateUserLogin() {
        user.setId(1L);
        user.setEmail("korben.sweetheart@gmail.com");
        user.setLogin("korben sweetheart");
        user.setName("Корбен Даллас");
        user.setBirthday(LocalDate.of(1955, 3, 19));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Логин не может содержать пробелы",
                violations.iterator().next().getMessage());
    }

    @Test
    void validateUserBirthday() {
        user.setId(1L);
        user.setEmail("korben.sweetheart@gmail.com");
        user.setLogin("korben.sweetheart");
        user.setName("Корбен Даллас");
        user.setBirthday(LocalDate.of(2955, 3, 19));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Дата рождения не может быть в будущем", violations.iterator().next().getMessage());
    }
}