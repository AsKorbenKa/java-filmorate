# java-filmorate

---
Template repository for Filmorate project.


## Схема базы данных

---
![ТЗ12 (6)](https://github.com/user-attachments/assets/d3305659-5166-456f-a16c-47196faf78e4)

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
