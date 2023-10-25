package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private Map<String, Object> getParams(User user) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("birthday", user.getBirthday());
        parameters.put("name", user.getName());
        parameters.put("login", user.getLogin());
        parameters.put("email", user.getEmail());
        parameters.put("user_id", user.getId());
        return parameters;
    }


    @Override
    public User createUser(User user) {
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate())
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        user.setId(simpleJdbcInsert.executeAndReturnKey(getParams(user)).intValue());

        return user;
    }


    @Override
    public void updateUser(User user) {
        String sql = "UPDATE users " +
                "SET name = :name, " +
                "email = :email, " +
                "login = :login, " +
                "birthday = :birthday " +
                "WHERE user_id = :user_id";

        int rows = namedParameterJdbcTemplate.update(sql, getParams(user));
        if (rows == 0) {
            log.warn("Пользователь с id {} не найден.", user.getId());
            throw new NotFoundException(
                    String.format("Пользователь с id %d не найден.", user.getId()));
        }
    }


    @Override
    public List<User> getUsers() {
        String sql = "SELECT * " +
                "FROM users";

        return namedParameterJdbcTemplate.getJdbcTemplate().query(sql, (rs, rowNum) -> new User(
                rs.getInt("user_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate()

        ));
    }


    @Override
    public Optional<User> getUserById(int id) {
        try {
            String sql = "SELECT * " +
                    "FROM users " +
                    "WHERE user_id = :user_id";
            Map<String, Object> mapper = new HashMap<>();
            mapper.put("user_id", id);
            return namedParameterJdbcTemplate.queryForObject(sql, mapper,
                    (rs, rowNum) -> Optional.of(new User(
                            rs.getInt("user_id"),
                            rs.getString("email"),
                            rs.getString("login"),
                            rs.getString("name"),
                            rs.getDate("birthday").toLocalDate()

                    )));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Пользователь с id %d не найден.", id));
        }
    }


    @Override
    public void addFriend(User user, User friend) {
        try {
            String sql = "MERGE " +
                    "INTO friends AS f " +
                    "USING VALUES (:user_id, :friend_id) AS val(user_id, friend_id) " +
                    "ON f.user_id = val.user_id " +
                    "AND f.friend_id = val.friend_id " +
                    "WHEN NOT MATCHED THEN " +
                    "INSERT " +
                    "VALUES (val.user_id, val.friend_id)";

            Map<String, Object> params = new HashMap<>();
            params.put("user_id", user.getId());
            params.put("friend_id", friend.getId());
            namedParameterJdbcTemplate.update(sql, params);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Неизвестный пользователь.");
        }
    }


    @Override
    public void removeFriend(User user, User friend) {
        try {
            String sql = "DELETE " +
                    "FROM friends " +
                    "WHERE user_id = :user_id " +
                    "AND friend_id = :friend_id";

            List<User> friend1 = getFriends(user);
            Map<String, Object> params = new HashMap<>();
            params.put("user_id", user.getId());
            params.put("friend_id", friend.getId());
            namedParameterJdbcTemplate.update(sql, params);
            List<User> friend2 = getFriends(user);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Неизвестный пользователь.");
        }
    }


    @Override
    public List<User> getFriends(User user) {
        try {
            String sql = "SELECT * " +
                    "FROM users AS u " +
                    "LEFT JOIN friends AS f ON f.friend_id = u.user_id " +
                    "WHERE f.user_id = :user_id";

            Map<String, Object> params = new HashMap<>();
            params.put("user_id", user.getId());

            return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) -> new User(
                    rs.getInt("user_id"),
                    rs.getString("email"),
                    rs.getString("login"),
                    rs.getString("name"),
                    rs.getDate("birthday").toLocalDate()

            ));

        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Пользователь с id %d не найден.", user.getId()));
        }
    }

    @Override
    public List<User> getSameFriend(User user, User otherUser) {
        String sql = "SELECT u.user_id," +
                "u.email," +
                "u.login," +
                "u.name," +
                "u.birthday " +
                "FROM friends AS f " +
                "JOIN friends AS f2 ON f2.user_id = :other_id " +
                "AND f2.friend_id = f.friend_id " +
                "JOIN users AS u ON u.user_id = f.friend_id " +
                "WHERE f.user_id = :user_id";

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", user.getId());
        params.put("other_id", otherUser.getId());

        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) -> new User(
                rs.getInt("user_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate()

        ));
    }
}
