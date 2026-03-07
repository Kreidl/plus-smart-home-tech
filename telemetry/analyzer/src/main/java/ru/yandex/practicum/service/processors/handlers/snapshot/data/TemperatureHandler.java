package ru.yandex.practicum.service.processors.handlers.snapshot.data;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.model.ConditionType;

@Component
public class TemperatureHandler implements SensorData {
    @Override
    public ConditionType getType() {
        return ConditionType.TEMPERATURE;
    }

    @Override
    public Integer getValue(Object snapshot) {
        return switch (snapshot) {
            case TemperatureSensorAvro temperatureSensorAvro -> temperatureSensorAvro.getTemperatureC();
            case ClimateSensorAvro climateSensorAvro -> climateSensorAvro.getTemperatureC();
            default -> throw new IllegalArgumentException("Unknown type with temperature");
        };
    }
}
