package ru.yandex.practicum.telemetry.collector.model.sensor;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NotNull
@ToString(callSuper = true)
public class TemperatureSensorEvent extends SensorEvent {
    private Integer temperatureC;
    private Integer temperatureF;

    @Override
    public SensorEventType getType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }
}
