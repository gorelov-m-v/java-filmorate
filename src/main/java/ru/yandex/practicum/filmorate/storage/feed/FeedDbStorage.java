package ru.yandex.practicum.filmorate.storage.feed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedDbStorage implements FeedStorage {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<Event> getFeedByUserId(Integer userId) {
        String sql = "SELECT * " +
                "FROM feed " +
                "WHERE user_id = :user_id";

        Map<String, Object> mapper = new HashMap<>();
        mapper.put("user_id", userId);

        return namedParameterJdbcTemplate.query(sql, mapper, rs -> {
            List<Event> list = new ArrayList<>();
            while (rs.next()) {
                Event event = new Event();
                event.setEventId(rs.getInt("event_id"));
                event.setTimestamp(rs.getLong("timestamp"));
                event.setUserId(rs.getInt("user_id"));
                event.setEventType(rs.getString("event_type"));
                event.setOperation(rs.getString("operation"));
                event.setEntityId(rs.getInt("entity_id"));
                list.add(event);
            }
            return list;
        });
    }

    @Override
    public Event addEvent(Integer userId, String eventType, String operation, Integer entityId) {
        Event event = new Event();
        event.setUserId(userId);
        event.setEventType(eventType);
        event.setOperation(operation);
        event.setEntityId(entityId);

        String sql = "INSERT INTO feed " +
                "(user_id, event_type, operation, entity_id, timestamp) " +
                "values(:user_id, :event_type, :operation, :entity_id, :timestamp)";

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("user_id", userId)
                .addValue("event_type", eventType)
                .addValue("operation", operation)
                .addValue("entity_id", entityId)
                .addValue("timestamp", System.currentTimeMillis());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, namedParameters, keyHolder);
        event.setEventId((Integer) keyHolder.getKey());

        log.info("Записано новое событие с id {} ", event.getEventId());

        return event;
    }
}
