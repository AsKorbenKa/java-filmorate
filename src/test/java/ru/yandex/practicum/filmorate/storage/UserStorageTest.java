package ru.yandex.practicum.filmorate.storage;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@ComponentScan("ru.yandex.practicum.filmorate")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserStorageTest {
    UserStorage userStorage;
    User newUser = new User();

    @BeforeEach
    public void beforeEach() {
        newUser.setName("Willy Wonka");
        newUser.setLogin("BigWonka69");
        newUser.setEmail("ww@gmail.com");
        newUser.setBirthday(LocalDate.of(1990, 3, 14));
    }

    @AfterEach
    public void afterEach() {
        userStorage.delete(newUser);
    }

    @Test
    public void testCreateUser() {
        assertThat(userStorage.create(newUser))
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", "Willy Wonka");
    }

    @Test
    public void testUpdateUser() {
        newUser.setId(1L);

        assertThat(userStorage.update(newUser))
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "Willy Wonka");
    }

    @Test
    public void testFindAll() {
        assertThat(userStorage.findAll()).isNotEmpty()
                .hasSize(3)
                .filteredOn("name", "Jack")
                .isNotEmpty()
                .hasExactlyElementsOfTypes(User.class);
    }

    @Test
    public void testFindUserById() {
        assertThat(userStorage.getUserById(2L))
                .isInstanceOf(User.class)
                .hasFieldOrPropertyWithValue("id", 2L)
                .hasFieldOrPropertyWithValue("name", "Jack");
    }

    @Test
    public void testFindUserFriends() {
        assertThat(userStorage.findAllFriends(2L))
                .isNotEmpty()
                .hasSize(2) // 1L, 3L
                .filteredOn("name", "Sparrow")
                .isNotEmpty()
                .hasExactlyElementsOfTypes(User.class);
    }

    @Test
    public void testFindAllMutualFriends() {
        assertThat(userStorage.findAllMutualFriends(1L, 3L))
                .isNotEmpty()
                .hasSize(1)
                .filteredOn("name", "Jack")
                .isNotEmpty()
                .hasExactlyElementsOfTypes(User.class);
    }

    @Test
    public void testAddFriend() {
        userStorage.create(newUser);
        userStorage.addFriend(2L, 4L);

        assertThat(userStorage.findAllFriends(2L))
                .isNotEmpty()
                .hasSize(3) // 1L, 3L, 4L
                .filteredOn("name", "Willy Wonka")
                .isNotEmpty()
                .hasExactlyElementsOfTypes(User.class);
    }

    @Test
    public void testRemoveFriend() {
        userStorage.removeFriend(2L, 1L);

        assertThat(userStorage.findAllFriends(2L))
                .isNotEmpty()
                .hasSize(1) // 1L
                .filteredOn("name", "Sparrow")
                .isNotEmpty()
                .hasExactlyElementsOfTypes(User.class);
    }
}
