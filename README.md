# java-filmorate

---
Template repository for Filmorate project.


## Схема базы данных

---
![Схема базы данных](https://github.com/user-attachments/assets/6b52728d-95a7-4165-9387-a7d8eb905983)

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
SELECT *
FROM user AS u
JOIN friendship AS f 
