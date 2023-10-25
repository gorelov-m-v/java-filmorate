DROP ALL OBJECTS;

CREATE TABLE IF NOT EXISTS users
(
    user_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email VARCHAR(100) NOT NULL,
    login VARCHAR(25) NOT NULL,
    name VARCHAR(50),
    birthday DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS genres
(
    genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    genre_name VARCHAR(40) NOT NULL
);

CREATE TABLE IF NOT EXISTS mpa_ratings
(
    mpa_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    mpa_name VARCHAR(40)
);

CREATE TABLE IF NOT EXISTS films
(
    film_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(25) NOT NULL,
    description VARCHAR(200) NOT NULL,
    release_date TIMESTAMP NOT NULL,
    likes INTEGER,
    duration INTEGER NOT NULL,
    mpa_id INTEGER REFERENCES mpa_ratings (mpa_id)
);

CREATE TABLE IF NOT EXISTS friends
(
    user_id INTEGER NOT NULL REFERENCES users (user_id),
    friend_id INTEGER NOT NULL REFERENCES users (user_id),
    CONSTRAINT PK_Friendship PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS likes
(
    film_id INTEGER NOT NULL REFERENCES films (film_id) ON DELETE CASCADE,
    user_id INTEGER NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT PK_Like PRIMARY KEY (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS film_genre
(
    film_id INTEGER NOT NULL REFERENCES films (film_id) ON DELETE CASCADE,
    genre_id INTEGER NOT NULL REFERENCES genres (genre_id)
);