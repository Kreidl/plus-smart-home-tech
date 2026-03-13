package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorProto;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.telemetry.collector.configuration.KafkaProducerConfig;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEventType;

import java.time.Instant;

@Component(value = "TEMPERATURE_SENSOR")
public class TemperatureEventHandler extends BaseSensorEventHandler<TemperatureSensorAvro> {

    public TemperatureEventHandler(KafkaProducerConfig kafkaProducerConfig) {
        super(kafkaProducerConfig);
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }

    @Override
    public SensorEventProto.PayloadCase getMessageTypeProto() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR;
    }

    @Override
    public TemperatureSensorAvro protoToAvro(SensorEventProto sensorEvent) {
        TemperatureSensorProto temperatureSensorProto = sensorEvent.getTemperatureSensor();
        Instant timestamp = Instant.ofEpochSecond(sensorEvent.getTimestamp().getSeconds(),
                sensorEvent.getTimestamp().getNanos());
        return TemperatureSensorAvro.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(timestamp)
                .setTemperatureC(temperatureSensorProto.getTemperatureC())
                .setTemperatureF(temperatureSensorProto.getTemperatureF())
                .build();
    }
}
