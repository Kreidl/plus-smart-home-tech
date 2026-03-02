package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.collector.configuration.KafkaProducerConfig;
import ru.yandex.practicum.telemetry.collector.model.hub.*;

import java.util.List;

@Component(value = "SCENARIO_ADDED")
public class ScenarioAddedEventHandler extends BaseHubEventHandler<ScenarioAddedEventAvro> {

    public ScenarioAddedEventHandler(KafkaProducerConfig kafkaProducerConfig) {
        super(kafkaProducerConfig);
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.SCENARIO_ADDED;
    }

    @Override
    public ScenarioAddedEventAvro mapToAvro(HubEvent hubEvent) {
        ScenarioAddedEvent scenarioAddedEvent = (ScenarioAddedEvent) hubEvent;
        List<ScenarioConditionAvro> conditions = scenarioAddedEvent.getConditions().stream()
                .map(scenarioCondition -> {
                    ScenarioConditionAvro.Builder builder = ScenarioConditionAvro.newBuilder()
                            .setSensorId(scenarioCondition.sensorId())
                            .setType(ConditionTypeAvro.valueOf(scenarioCondition.type().name()))
                            .setOperation(ConditionOperationAvro.valueOf(scenarioCondition.operation().name()));
                    builder.setValue(scenarioCondition.value());
                    return builder.build();
                })
                .toList();
        List<DeviceActionAvro> actions = scenarioAddedEvent.getActions().stream()
                .map(deviceAction -> {
                    DeviceActionAvro.Builder builder = DeviceActionAvro.newBuilder()
                            .setSensorId(deviceAction.sensorId())
                            .setType(ActionTypeAvro.valueOf(deviceAction.type().name()));
                    return builder.build();
                })
                .toList();
        return ScenarioAddedEventAvro.newBuilder()
                .setName(scenarioAddedEvent.getName())
                .setConditions(conditions)
                .setActions(actions)
                .build();
    }
}
