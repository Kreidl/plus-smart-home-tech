package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
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
    public HubEventProto.PayloadCase getMessageTypeProto() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    public ScenarioAddedEventAvro protoToAvro(HubEventProto hubEvent) {
        ScenarioAddedEventProto scenarioAddedEventProto = hubEvent.getScenarioAdded();
        List<ScenarioConditionAvro> conditions = scenarioAddedEventProto.getConditionList().stream()
                .map(scenarioCondition -> {
                    ScenarioConditionAvro.Builder builder = ScenarioConditionAvro.newBuilder()
                            .setSensorId(scenarioCondition.getSensorId())
                            .setType(ConditionTypeAvro.valueOf(scenarioCondition.getType().name()))
                            .setOperation(ConditionOperationAvro.valueOf(scenarioCondition.getOperation().name()));
                    switch (scenarioCondition.getValueCase()) {
                        case BOOL_VALUE:
                            builder.setValue(scenarioCondition.getBoolValue() ? 1 : 0);
                            break;
                        case INT_VALUE:
                            builder.setValue(scenarioCondition.getIntValue());
                            break;
                    }
                    return builder.build();
                })
                .toList();
        List<DeviceActionAvro> actions = scenarioAddedEventProto.getActionList().stream()
                .map(deviceAction -> {
                    DeviceActionAvro.Builder builder = DeviceActionAvro.newBuilder()
                            .setSensorId(deviceAction.getSensorId())
                            .setType(ActionTypeAvro.valueOf(deviceAction.getType().name()));
                    return builder.build();
                })
                .toList();
        return ScenarioAddedEventAvro.newBuilder()
                .setName(scenarioAddedEventProto.getName())
                .setConditions(conditions)
                .setActions(actions)
                .build();
    }
}
