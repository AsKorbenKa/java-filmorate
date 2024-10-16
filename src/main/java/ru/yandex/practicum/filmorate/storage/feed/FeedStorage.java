package ru.yandex.practicum.filmorate.storage.feed;

import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.enums.Operation;
import ru.yandex.practicum.filmorate.model.Feed;

import java.util.Collection;

public interface FeedStorage {
    // получение Ленты событий по id пользователя
    Collection<Feed> findFeedByUserId(Long userId);

    // добавление события в базу данных
    void addFeed(Long entityId, Long userId, EventType eventType, Operation operation);
}
