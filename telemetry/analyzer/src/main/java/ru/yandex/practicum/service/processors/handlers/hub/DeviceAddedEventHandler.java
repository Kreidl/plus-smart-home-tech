package ru.yandex.practicum.service.processors.handlers.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.model.HubEventType;
import ru.yandex.practicum.model.Sensor;
import ru.yandex.practicum.repository.SensorRepository;
import ru.yandex.practicum.service.processors.mapper.EntityMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceAddedEventHandler implements HubEventHandler {
    private final SensorRepository sensorRepository;
    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED;
    }

    @Override
    public String getPayloadType() {
        return DeviceAddedEventAvro.class.getSimpleName();
    }

    @Override
    public void handle(HubEventAvro hubEventAvro) {
        DeviceAddedEventAvro deviceAddedEventAvro = (DeviceAddedEventAvro) hubEventAvro.getPayload();
        Sensor sensor = EntityMapper.deviceAddedAvroToSensor(deviceAddedEventAvro, hubEventAvro.getHubId());
        sensorRepository.save(sensor);
        log.info("Device added {}", sensor);
    }
}
