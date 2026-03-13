package ru.yandex.practicum.service.processors.handlers.snapshot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.HubRouterGrpcClient;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.ConditionOperation;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.service.processors.handlers.snapshot.data.SensorData;
import ru.yandex.practicum.service.processors.handlers.snapshot.data.SensorDataFactory;
import ru.yandex.practicum.service.processors.mapper.EntityMapper;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotHandler {
    private final HubRouterGrpcClient hubRouterGrpcClient;
    private final ActionRepository actionRepository;
    private final ConditionRepository conditionRepository;
    private final ScenarioRepository scenarioRepository;
    private final SensorDataFactory sensorDataFactory;

    public void handle(SensorsSnapshotAvro sensorsSnapshotAvro) {
        log.debug("Start handle snapshot {}", sensorsSnapshotAvro);
        Map<String, SensorStateAvro> sensorStateAvroMap = sensorsSnapshotAvro.getSensorsState();
        List<Scenario> scenarios = scenarioRepository.findByHubId(sensorsSnapshotAvro.getHubId());
        log.info("Found {} scenarios for hub {}", scenarios.size(), sensorsSnapshotAvro.getHubId());
        scenarios.stream()
                .filter(scenario -> {
                    List<Condition> conditions = conditionRepository.findAllByScenario(scenario);
                    return conditions.stream()
                            .allMatch(condition -> checkCondition(condition, sensorStateAvroMap));
                })
                .forEach(scenario -> {
                    log.info("Send actions from scenario {}", scenario);
                    sendActions(scenario);
                });
    }

    private Boolean checkCondition(Condition condition, Map<String, SensorStateAvro> sensorStateAvroMap) {
        SensorStateAvro sensorStateAvro = sensorStateAvroMap.get(condition.getSensor().getId());
        if (sensorStateAvro == null) {
            return false;
        }
        SensorData sensorData = sensorDataFactory.getSensorData(condition.getType());
        Integer currentValue = sensorData.getValue(sensorStateAvro.getData());
        return checkOperation(condition, currentValue);
    }

    private Boolean checkOperation(Condition condition, Integer currentValue) {
        ConditionOperation conditionOperation = condition.getOperation();
        Integer targetValue = condition.getValue();
        switch (conditionOperation) {
            case EQUALS -> {
                return targetValue.equals(currentValue);
            }
            case LOWER_THAN -> {
                return currentValue < targetValue;
            }
            case GREATER_THAN -> {
                return currentValue > targetValue;
            }
            case null -> {
                return null;
            }
        }
    }

    private void sendActions(Scenario scenario) {
        for (Action action : actionRepository.findAllByScenarioId(scenario.getId())) {
            try {
                DeviceActionRequest request = EntityMapper.actionToActionRequest(action);
                hubRouterGrpcClient.send(request);
                log.debug("Sent action: {}", request);
            } catch (Exception e) {
                log.error("Failed to send action {} for scenario {}", action.getId(), scenario.getId(), e);
            }
        }
    }
}
