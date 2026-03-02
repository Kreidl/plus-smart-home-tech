package ru.yandex.practicum.telemetry.collector.model.sensor;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NotNull
@ToString(callSuper = true)
public class LightSensorEvent extends SensorEvent {
    private Integer linkQuality;
    private Integer luminosity;

    @Override
    public SensorEventType getType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }
}
