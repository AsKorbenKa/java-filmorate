SET REFERENTIAL_INTEGRITY FALSE;

TRUNCATE TABLE FILM_GENRES;

TRUNCATE TABLE FRIENDSHIP;

TRUNCATE TABLE FILM_LIKES;

TRUNCATE TABLE MPA_RATINGS;

TRUNCATE TABLE FILMS RESTART IDENTITY;

TRUNCATE TABLE USERS RESTART IDENTITY;

TRUNCATE TABLE GENRE RESTART IDENTITY;

TRUNCATE TABLE FILM_RATING RESTART IDENTITY;

SET REFERENTIAL_INTEGRITY TRUE;

INSERT INTO FILM_RATING (NAME)
VALUES ('G'),                     -- 1
    ('PG'),              -- 2
    ('PG-13'),                   -- 3
    ('R'),  -- 4
    ('NC-17');                     -- 5


INSERT INTO GENRE (NAME)
VALUES ('Комедия'),     -- 1
    ('Драма'),       -- 2
    ('Мультфильм'),    -- 3
    ('Триллер'),    -- 4
    ('Документальный'), -- 5
    ('Боевик');      -- 6
    --('Фантастика'),    -- 7
    --('Фэнтези'),    -- 8
    --('Приключения'),  -- 9
    --('Криминал'),    -- 10
    --('Семейный'),    -- 11
    --('Детектив');    -- 12

INSERT INTO FILMS (NAME , DESCRIPTION , RELEASEDATE , DURATION)
VALUES ('Тень', '30-ые годы XX века, город Нью-Йорк...', '1994-07-01', 108),
    ('Звёздные войны: Эпизод 4 – Новая надежда', 'Татуин. Планета-пустыня. Уже постаревший рыцарь Джедай ...', '1997-05-25', 121),
    ('Зеленая миля', 'Пол Эджкомб — начальник блока смертников в тюрьме «Холодная гора» ...', '1999-12-06', 189),
    ('Гадкий я', 'Гадкий снаружи, но добрый внутри Грю намерен, тем не менее, ...', '2010-06-27', 95);

INSERT INTO MPA_RATINGS (FILM_ID, RATING_ID)
VALUES (1, 3),
    (2, 2),
    (3, 4),
    (4, 2);

INSERT INTO FILM_GENRES (FILM_ID , GENRE_ID)
VALUES (1, 1), (1, 3), (1, 5),
    (2, 3), (2, 4), (2, 5), (2, 6),
    (3, 2), (3, 5), (3, 6),
    (4, 1), (4, 2), (4, 3), (4, 4), (4, 5), (4, 6);
--VALUES (1, 6), (1, 7), (1, 12),
--    (2, 6), (2, 7), (2, 8), (2, 9),
--    (3, 2), (3, 8), (3, 10),
--    (4, 1), (4, 3), (4, 7), (4, 9), (4, 10), (4, 11);

INSERT INTO USERS (EMAIL , LOGIN , NAME , BIRTHDAY)
VALUES ('Capitan@yandex.ru', 'Capitan', 'Capitan', '2001-01-01'),   -- 1
    ('Jack@yandex.ru', 'Jack', 'Jack', '2002-02-02'),       -- 2
    ('Sparrow@yandex.ru', 'Sparrow', 'Sparrow', '2003-03-03');   -- 3

INSERT INTO FILM_LIKES (FILM_ID, USER_ID)
VALUES (1, 1), (1, 3),
    (2, 1), (2, 2), (2, 3),
    (3, 2),
    (4, 1), (4, 2), (4, 3);

INSERT INTO FRIENDSHIP (USER_ID, FRIEND_ID, STATUS)
VALUES (1, 2, 'Confirmed'), (2, 1, 'Confirmed'),
    (2, 3, 'Confirmed'), (3, 1, 'Not confirmed'),
    (3, 2, 'Confirmed');