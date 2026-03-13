package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.telemetry.collector.configuration.KafkaProducerConfig;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEventType;

@Component(value = "LIGHT_SENSOR")
public class LightEventHandler extends BaseSensorEventHandler<LightSensorAvro> {

    public LightEventHandler(KafkaProducerConfig kafkaProducerConfig) {
        super(kafkaProducerConfig);
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }

    @Override
    public SensorEventProto.PayloadCase getMessageTypeProto() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR;
    }

    @Override
    public LightSensorAvro protoToAvro(SensorEventProto sensorEvent) {
        LightSensorProto lightSensorProto = sensorEvent.getLightSensor();
        return LightSensorAvro.newBuilder()
                .setLinkQuality(lightSensorProto.getLinkQuality())
                .setLuminosity(lightSensorProto.getLuminosity())
                .build();
    }
}
