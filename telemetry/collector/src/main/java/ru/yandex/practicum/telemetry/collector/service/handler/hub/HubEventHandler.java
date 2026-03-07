package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEventType;

public interface HubEventHandler {
    HubEventType getMessageType();

    HubEventProto.PayloadCase getMessageTypeProto();

    void handle(HubEventProto hubEvent);
}
