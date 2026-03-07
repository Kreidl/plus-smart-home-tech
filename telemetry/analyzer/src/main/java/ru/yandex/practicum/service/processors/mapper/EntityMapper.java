package ru.yandex.practicum.service.processors.mapper;

import com.google.protobuf.Timestamp;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.model.*;

import java.time.Instant;

@Slf4j
@UtilityClass
public class EntityMapper {
    public Sensor deviceAddedAvroToSensor (DeviceAddedEventAvro deviceAddedEventAvro, String hubId) {
        Sensor sensor = new Sensor();
        sensor.setId(deviceAddedEventAvro.getId());
        sensor.setHubId(hubId);
        return sensor;
    }

    public Action actionAvroToEntity(Scenario scenario, DeviceActionAvro deviceActionAvro, Sensor sensor) {
        log.info("Start convert deviceActionAvro {} to Action", deviceActionAvro);
        Action action = new Action();
        action.setType(ActionType.valueOf(deviceActionAvro.getType().name()));
        action.setSensor(sensor);
        action.setScenario(scenario);
        action.setValue(deviceActionAvro.getValue());
        log.info("End convert deviceActionAvro to Action");
        return action;
    }

    public Condition conditionAvroToEntity(Scenario scenario, ScenarioConditionAvro scenarioConditionAvro, Sensor sensor) {
        Condition condition = new Condition();
        condition.setType(ConditionType.valueOf(scenarioConditionAvro.getType().name()));
        condition.setOperation(ConditionOperation.valueOf(scenarioConditionAvro.getOperation().name()));
        condition.setSensor(sensor);
        condition.setScenario(scenario);
        Object value = scenarioConditionAvro.getValue();
        if (value.getClass() == Integer.class) {
            condition.setValue((int) value);
        } else {
            condition.setValue(Boolean.TRUE.equals(value) ? 1 : 0);
        }
        return condition;
    }

    public DeviceActionRequest actionToActionRequest(Action action) {
        log.info("Start convert Action to DeviceActionRequest");
        Instant timestamp = Instant.now();
        DeviceActionRequest deviceActionRequest = DeviceActionRequest.newBuilder()
                .setHubId(action.getScenario().getHubId())
                .setScenarioName(action.getScenario().getName())
                .setAction(actionToProto(action))
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(timestamp.getEpochSecond())
                        .setNanos(timestamp.getNano()).build())
                .build();
        log.info("End convert Action to DeviceActionRequest, result: {}", deviceActionRequest);
        return deviceActionRequest;
    }

    private static DeviceActionProto actionToProto(Action action) {
        DeviceActionProto.Builder builder = DeviceActionProto.newBuilder()
                .setSensorId(action.getSensor().getId())
                .setType(ActionTypeProto.valueOf(action.getType().name()));
        if (action.getValue() != null) {
            builder.setValue(action.getValue());
        }
        return builder.build();
    }
}
