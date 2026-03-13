package ru.yandex.practicum.service.processors.handlers.snapshot.data;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.model.ConditionType;

@Component
public class LuminosityHandler implements SensorData {
    @Override
    public ConditionType getType() {
        return ConditionType.LUMINOSITY;
    }

    @Override
    public Integer getValue(Object snapshot) {
        LightSensorAvro lightSensorAvro = (LightSensorAvro) snapshot;
        return lightSensorAvro.getLuminosity();
    }
}
