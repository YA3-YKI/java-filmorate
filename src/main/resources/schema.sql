-- Таблица пользователей
CREATE TABLE IF NOT EXISTS USERS (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    login VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255),
    birthday DATE NOT NULL
);

-- Таблица фильмов
CREATE TABLE IF NOT EXISTS FILMS (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    release_date DATE NOT NULL,
    duration INT NOT NULL
);

-- Таблица лайков фильмов (многие ко многим: Film ↔ User)
CREATE TABLE IF NOT EXISTS FILM_LIKES (
    film_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY(film_id, user_id),
    FOREIGN KEY(film_id) REFERENCES FILMS(id) ON DELETE CASCADE,
    FOREIGN KEY(user_id) REFERENCES USERS(id) ON DELETE CASCADE
);

-- Таблица друзей пользователей (Friendship)
CREATE TABLE IF NOT EXISTS FRIENDS (
    user_id BIGINT NOT NULL,
    friend_id BIGINT NOT NULL,
    PRIMARY KEY(user_id, friend_id),
    FOREIGN KEY(user_id) REFERENCES USERS(id) ON DELETE CASCADE,
    FOREIGN KEY(friend_id) REFERENCES USERS(id) ON DELETE CASCADE
);

-- Таблица жанров (если GenreController оставляем для будущих расширений)
CREATE TABLE IF NOT EXISTS GENRES (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

-- Таблица MPA рейтингов (для MpaController)
CREATE TABLE IF NOT EXISTS MPA_RATINGS (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

-- Таблица связи фильмов и жанров
CREATE TABLE IF NOT EXISTS FILM_GENRES (
    film_id BIGINT NOT NULL,
    genre_id BIGINT NOT NULL,
    PRIMARY KEY(film_id, genre_id),
    FOREIGN KEY(film_id) REFERENCES FILMS(id) ON DELETE CASCADE,
    FOREIGN KEY(genre_id) REFERENCES GENRES(id) ON DELETE CASCADE
);
