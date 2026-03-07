package ru.yandex.practicum.service.processors.handlers.snapshot.data;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.model.ConditionType;

@Component
public class HumidityHandler implements SensorData {
    @Override
    public ConditionType getType() {
        return ConditionType.HUMIDITY;
    }

    @Override
    public Integer getValue(Object snapshot) {
        ClimateSensorAvro climateSensorAvro = (ClimateSensorAvro) snapshot;
        return climateSensorAvro.getHumidity();
    }
}
