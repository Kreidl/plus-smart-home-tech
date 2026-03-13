package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEventType;

public interface SensorEventHandler {
    SensorEventType getMessageType();

    SensorEventProto.PayloadCase getMessageTypeProto();

    void handle(SensorEventProto sensorEventProto);
}
