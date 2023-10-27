package ru.yandex.practicum.filmorate.storage.feed;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface FeedStorage {

    List<Event> getFeedByUserId(Integer userId);

    Event addEvent(Integer userId, String eventType, String operation, Integer entityId);
}
