package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.telemetry.collector.configuration.KafkaProducerConfig;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEventType;

@Component(value = "SWITCH_SENSOR")
public class SwitchEventHandler extends BaseSensorEventHandler<SwitchSensorAvro> {

    public SwitchEventHandler(KafkaProducerConfig kafkaProducerConfig) {
        super(kafkaProducerConfig);
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.SWITCH_SENSOR_EVENT;
    }

    @Override
    public SensorEventProto.PayloadCase getMessageTypeProto() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR;
    }

    @Override
    public SwitchSensorAvro protoToAvro(SensorEventProto sensorEvent) {
        SwitchSensorProto switchSensorProto = sensorEvent.getSwitchSensor();
        return SwitchSensorAvro.newBuilder()
                .setState(switchSensorProto.getState())
                .build();
    }
}
