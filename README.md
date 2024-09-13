# java-filmorate

---
Template repository for Filmorate project.


## Схема базы данных

---
![ТЗ12 (2)](https://github.com/user-attachments/assets/b7926de0-0e1a-41d7-8493-f92352a44d0d)

## SQL запросы для модели User

### 1. Найти всех пользователей
### findAll()
```
SELECT *
FROM user;
```
### 2. Создать пользователя
### create(User user)
```
INSERT INTO user (name, login, email, birthday)
VALUE ({user.getEmail()}, {user.getLogin()}, {user.getName()}, {user.getBirthday()});
```
### 3. Обновить данные пользователя
### update(User newUser)
```
UPDATE user 
SET email = {user.getEmail()}, 
    login = {user.getLogin()}, 
    name = {user.getName()}, 
    birthday = {user.getBirthday()}
WHERE id = {user.getId()};
```
### 4. Найти всех друзей пользователя
### findAllFriends(Long userId)
```
SELECT f.*
FROM user AS u
JOIN friendship AS f ON u.user_id=f.user_id
WHERE u.user_id = {user_id} AND f.status = 'accepted';
```
### 5. Найти общих друзей двух пользователей
### findAllMutualFriends(Long userId, Long otherId)
```
SELECT
FROM user AS u
JOIN friendship AS f ON u.user_id=f.user_id
```
