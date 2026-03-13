package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.telemetry.collector.configuration.KafkaProducerConfig;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEventType;

@Component(value = "CLIMATE_SENSOR")
public class ClimateEventHandler extends BaseSensorEventHandler<ClimateSensorAvro>{

    public ClimateEventHandler(KafkaProducerConfig kafkaProducerConfig) {
        super(kafkaProducerConfig);
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }

    @Override
    public SensorEventProto.PayloadCase getMessageTypeProto() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR;
    }

    @Override
    public ClimateSensorAvro protoToAvro(SensorEventProto sensorEvent) {
        ClimateSensorProto climateSensorProto = sensorEvent.getClimateSensor();
        return ClimateSensorAvro.newBuilder()
                .setTemperatureC(climateSensorProto.getTemperatureC())
                .setHumidity(climateSensorProto.getHumidity())
                .setCo2Level(climateSensorProto.getCo2Level())
                .build();
    }
}
