package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Event {
    private Integer timestamp;
    private Integer userId;
    private String eventType;
    private String operation;
    private Integer eventId;
    private Integer entityId;
}

