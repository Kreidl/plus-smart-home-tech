package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.telemetry.collector.configuration.KafkaProducerConfig;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEventType;

@Component(value = "DEVICE_ADDED")
public class DeviceAddedEventHandler extends BaseHubEventHandler<DeviceAddedEventAvro> {

    public DeviceAddedEventHandler(KafkaProducerConfig kafkaProducerConfig) {
        super(kafkaProducerConfig);
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.DEVICE_ADDED;
    }

    @Override
    public HubEventProto.PayloadCase getMessageTypeProto() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    @Override
    public DeviceAddedEventAvro protoToAvro(HubEventProto hubEvent) {
        DeviceAddedEventProto deviceAddedEventProto = hubEvent.getDeviceAdded();
        return DeviceAddedEventAvro.newBuilder()
                .setId(deviceAddedEventProto.getId())
                .setType(DeviceTypeAvro.valueOf(deviceAddedEventProto.getType().name()))
                .build();
    }
}