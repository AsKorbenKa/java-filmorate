# Filmorate - сообщество для оценки и рекомендации фильмов

Java, Spring Boot, Spring MVC, SQL, H2, REST API, JUnit
# О проекте
Filmorate - веб-приложение, в котором можно ставить оценки фильмам и выбирать кино на основе оценок других пользователей.

# Что умеет Filmorate?

- выводить топ лучших фильмов по версии пользователей;
- отображать фильмы по жанрам;
- разделять фильмы по рейтингу Ассоциации кинокомпаний;
- у вас будет собственный профиль, где будут храниться все понравившиеся вам фильмы;
- вы можете добавлять в друзья других пользователей.

# ER-диаграмма для проекта

![ТЗ12](https://github.com/user-attachments/assets/66f38049-17a3-41ab-b5e5-81b817f30775)

## SQL запросы для модели User

### 1. Найти всех пользователей
### findAll()
```
SELECT *
FROM users;
```
### 2. Создать пользователя
### create(User user)
```
INSERT INTO users (name, login, email, birthday)
VALUE ({user.getEmail()}, {user.getLogin()}, {user.getName()}, {user.getBirthday()});
```
### 3. Обновить данные пользователя
### update(User newUser)
```
UPDATE users 
SET email = {user.getEmail()}, 
    login = {user.getLogin()}, 
    name = {user.getName()}, 
    birthday = {user.getBirthday()}
WHERE id = {user.getId()};
```
### 4. Найти всех друзей пользователя
### findAllFriends(Long userId)
```
SELECT *
FROM users
WHERE user_id in (SELECT friend_id
                  FROM friendship
                  WHERE user_id = ? AND status = 'Confirmed')
```
### 5. Найти общих друзей двух пользователей
### findAllMutualFriends(Long userId, Long otherId)
```
SELECT *
FROM users AS u
WHERE u.USER_ID IN (SELECT f2.FRIEND_ID
                    FROM FRIENDSHIP AS f2
                    WHERE f2.USER_ID = ? AND f2.STATUS = 'Confirmed'
                    INTERSECT
                    SELECT FRIEND_ID
                    FROM FRIENDSHIP AS f
                    WHERE f.USER_ID = ? AND f.STATUS = 'Confirmed');
```
### 6. Добавить нового друга
### addFriend(Long userId, Long friendId)
```
INSERT INTO friendship (user_id, friend_id, status) VALUES (?, ?, 'Not confirmed')
```
### 7. Удалить друга
### removeFriend(Long userId, Long friendId)
```
DELETE FROM friendship WHERE user_id = ? AND friend_id = ?
```

## SQL запросы для модели Film
### 1. Получить список всех фильмов.
### findAll()
```
SELECT * FROM films
```
### 2. Добавить новый фильм в базу данных.
### create(CreateFilmDto film)
```
INSERT INTO films (name, description, releasedate, duration) VALUES (?, ?, ?, ?)
```
### 3. Обновить данные фильма.
### update(FilmDto newFilm)
```
UPDATE films SET name = ?, description = ?, releasedate = ?, duration = ? WHERE film_id = ?
```
### 4. Получение фильма по его id.
### getFilmById(Long filmId)
```
SELECT * FROM films WHERE film_id = ?
```
### 5. Получить список самых популярных фильмов.
### findMostPopularFilms(int count)
```
SELECT f.*
FROM film_likes AS fl 
JOIN films AS f ON fl.film_id=f.film_id
GROUP BY fl.film_id
ORDER BY COUNT(fl.user_id) DESC
LIMIT ?
```
### 6. Поставить лайк фильму.
### addLike(Long filmId, Long userId)
```
INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)
```
### 7. Удалить лайк.
### removeLike(Long filmId, Long userId)
```
DELETE FROM film_likes WHERE film_id = ? AND user_id = ?
```
