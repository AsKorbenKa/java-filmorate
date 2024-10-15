DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS friendship CASCADE;
DROP TABLE IF EXISTS film_rating CASCADE;
DROP TABLE IF EXISTS films CASCADE;
DROP TABLE IF EXISTS film_likes CASCADE;
DROP TABLE IF EXISTS mpa_ratings CASCADE;
DROP TABLE IF EXISTS genre CASCADE;
DROP TABLE IF EXISTS film_genres CASCADE;
DROP TABLE IF EXISTS feeds CASCADE;

CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(40) NOT NULL,
    login VARCHAR(40) NOT NULL,
    email VARCHAR(255) NOT NULL,
    birthday DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS friendship (
    user_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    friend_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    status VARCHAR(40) NOT NULL,
    PRIMARY KEY(user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS film_rating (
    rating_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(40) NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
    film_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(40) NOT NULL,
    description VARCHAR(255) NOT NULL,
    releaseDate DATE NOT NULL,
    duration BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS film_likes (
    film_id BIGINT NOT NULL REFERENCES films(film_id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    PRIMARY KEY(film_id, user_id)
);

CREATE TABLE IF NOT EXISTS mpa_ratings (
    film_id BIGINT NOT NULL REFERENCES films(film_id) ON DELETE CASCADE,
    rating_id BIGINT NOT NULL REFERENCES film_rating(rating_id) ON DELETE CASCADE,
    PRIMARY KEY(film_id, rating_id)
);

CREATE TABLE IF NOT EXISTS genre (
    genre_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(40) NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genres (
    film_id BIGINT NOT NULL REFERENCES films(film_id) ON DELETE CASCADE,
    genre_id BIGINT NOT NULL REFERENCES genre(genre_id) ON DELETE CASCADE,
    PRIMARY KEY(film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS directors (
    director_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(40) NOT NULL
);

CREATE TABLE IF NOT EXISTS film_directors (
    film_id BIGINT NOT NULL REFERENCES films(film_id) ON DELETE CASCADE,
    director_id BIGINT NOT NULL REFERENCES directors(director_id) ON DELETE CASCADE,
    PRIMARY KEY(film_id, director_id)
);

CREATE TABLE IF NOT EXISTS reviews (
    review_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    content TEXT NOT NULL,
    is_positive BOOLEAN NOT NULL,
    user_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    film_id BIGINT NOT NULL REFERENCES films(film_id) ON DELETE CASCADE,
    useful INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS reviews_likes (
    review_id BIGINT NOT NULL REFERENCES reviews(review_id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    is_like BOOLEAN,
    PRIMARY KEY(review_id, user_id)
);

--Создание таблицы хранения событий
CREATE TABLE IF NOT EXISTS feeds (
    event_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    entity_id BIGINT NOT NULL,
    event_type VARCHAR(50) CHECK (event_type IN ('LIKE', 'FRIEND', 'REVIEW')),
    operation VARCHAR(50) CHECK (operation IN ('ADD', 'REMOVE', 'UPDATE')),
    timestamp_feed TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
