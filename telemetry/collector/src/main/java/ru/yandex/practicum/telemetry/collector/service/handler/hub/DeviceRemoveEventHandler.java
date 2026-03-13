package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.telemetry.collector.configuration.KafkaProducerConfig;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEventType;

@Component(value = "DEVICE_REMOVED")
public class DeviceRemoveEventHandler extends BaseHubEventHandler<DeviceRemovedEventAvro> {

    public DeviceRemoveEventHandler(KafkaProducerConfig kafkaProducerConfig) {
        super(kafkaProducerConfig);
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.DEVICE_REMOVED;
    }

    @Override
    public HubEventProto.PayloadCase getMessageTypeProto() {
        return HubEventProto.PayloadCase.DEVICE_REMOVED;
    }

    @Override
    public DeviceRemovedEventAvro protoToAvro(HubEventProto hubEvent) {
        DeviceRemovedEventProto deviceRemovedEventProto = hubEvent.getDeviceRemoved();
        return DeviceRemovedEventAvro.newBuilder()
                .setId(deviceRemovedEventProto.getId())
                .build();
    }
}
