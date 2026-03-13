package ru.yandex.practicum.service.processors.handlers.hub;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.model.HubEventType;

public interface HubEventHandler {
    HubEventType getType();

    void handle(HubEventAvro hubEventAvro);

    String getPayloadType();
}
