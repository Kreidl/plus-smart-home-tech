package ru.yandex.practicum.service.processors.handlers.snapshot.data;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.model.ConditionType;

@Component
public class MotionHandler implements SensorData {
    @Override
    public ConditionType getType() {
        return ConditionType.MOTION;
    }

    @Override
    public Integer getValue(Object snapshot) {
        MotionSensorAvro motionSensorAvro = (MotionSensorAvro) snapshot;
        return motionSensorAvro.getMotion() ? 1 : 0;
    }
}
