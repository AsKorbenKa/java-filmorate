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
