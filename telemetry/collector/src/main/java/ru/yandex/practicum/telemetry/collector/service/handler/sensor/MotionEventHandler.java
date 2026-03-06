package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.telemetry.collector.configuration.KafkaProducerConfig;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEventType;

@Component(value = "MOTION_SENSOR")
public class MotionEventHandler extends BaseSensorEventHandler<MotionSensorAvro> {

    public MotionEventHandler(KafkaProducerConfig kafkaProducerConfig) {
        super(kafkaProducerConfig);
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }

    @Override
    public SensorEventProto.PayloadCase getMessageTypeProto() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR;
    }

    @Override
    public MotionSensorAvro protoToAvro(SensorEventProto sensorEvent) {
        MotionSensorProto motionSensorProto = sensorEvent.getMotionSensor();
        return MotionSensorAvro.newBuilder()
                .setMotion(motionSensorProto.getMotion())
                .setLinkQuality(motionSensorProto.getLinkQuality())
                .setVoltage(motionSensorProto.getVoltage())
                .build();
    }
}
