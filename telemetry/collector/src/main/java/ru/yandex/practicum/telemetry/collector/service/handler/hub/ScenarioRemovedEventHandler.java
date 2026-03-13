package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioRemovedEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.telemetry.collector.configuration.KafkaProducerConfig;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEventType;

@Component(value = "SCENARIO_REMOVED")
public class ScenarioRemovedEventHandler extends BaseHubEventHandler<ScenarioRemovedEventAvro> {

    public ScenarioRemovedEventHandler(KafkaProducerConfig kafkaProducerConfig) {
        super(kafkaProducerConfig);
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.SCENARIO_REMOVED;
    }

    @Override
    public HubEventProto.PayloadCase getMessageTypeProto() {
        return HubEventProto.PayloadCase.SCENARIO_REMOVED;
    }

    @Override
    public ScenarioRemovedEventAvro protoToAvro(HubEventProto hubEvent) {
        ScenarioRemovedEventProto scenarioRemovedEventProto = hubEvent.getScenarioRemoved();
        return ScenarioRemovedEventAvro.newBuilder()
                .setName(scenarioRemovedEventProto.getName())
                .build();
    }
}
