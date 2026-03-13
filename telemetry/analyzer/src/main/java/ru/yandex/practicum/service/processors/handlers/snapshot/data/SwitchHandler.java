package ru.yandex.practicum.service.processors.handlers.snapshot.data;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.model.ConditionType;

@Component
public class SwitchHandler implements SensorData {
    @Override
    public ConditionType getType() {
        return ConditionType.SWITCH;
    }

    @Override
    public Integer getValue(Object snapshot) {
        SwitchSensorAvro switchSensorAvro = (SwitchSensorAvro) snapshot;
        return switchSensorAvro.getState() ? 1 : 0;
    }
}
