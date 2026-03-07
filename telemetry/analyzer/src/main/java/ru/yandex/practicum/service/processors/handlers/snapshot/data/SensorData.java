package ru.yandex.practicum.service.processors.handlers.snapshot.data;

import ru.yandex.practicum.model.ConditionType;

public interface SensorData {
    ConditionType getType();

    Integer getValue(Object snapshot);
}
