package ru.yandex.practicum.service.processors.handlers.snapshot.data;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.ConditionType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SensorDataFactory {
    private Map<ConditionType, SensorData> sensorDataMap = new HashMap<>();

    public SensorDataFactory(List<SensorData> sensorDataList) {
        for (SensorData sensorData : sensorDataList) {
            this.sensorDataMap.put(sensorData.getType(), sensorData);
        }
    }

    public SensorData getSensorData(ConditionType conditionType) {
        if (sensorDataMap.containsKey(conditionType)) {
            return sensorDataMap.get(conditionType);
        } else {
            throw new IllegalArgumentException("Data type " + conditionType + " not found");
        }
    }
}
