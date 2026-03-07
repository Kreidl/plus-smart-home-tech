package ru.yandex.practicum.service.processors.handlers.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.repository.SensorRepository;
import ru.yandex.practicum.service.processors.mapper.EntityMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioAddedEventHandler implements HubEventHandler {
    private final ScenarioRepository scenarioRepository;
    private final SensorRepository sensorRepository;
    private final ActionRepository actionRepository;
    private final ConditionRepository conditionRepository;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }

    @Override
    public String getPayloadType() {
        return ScenarioAddedEventAvro.class.getSimpleName();
    }

    @Override
    @Transactional
    public void handle(HubEventAvro hubEventAvro) {
        ScenarioAddedEventAvro scenarioAddedEventAvro = (ScenarioAddedEventAvro) hubEventAvro.getPayload();
        Optional<Scenario> scenarioOpt = scenarioRepository.findByHubIdAndName(hubEventAvro.getHubId(),
                scenarioAddedEventAvro.getName());
        if (scenarioOpt.isPresent()) {
            updateScenario(scenarioOpt.get(), scenarioAddedEventAvro, hubEventAvro.getHubId());
        } else {
            addScenario(hubEventAvro.getHubId(), scenarioAddedEventAvro);
        }
    }

    public void updateScenario(Scenario scenario, ScenarioAddedEventAvro scenarioAddedEventAvro, String hubId) {
        scenario.setHubId(hubId);
        scenario.setName(scenarioAddedEventAvro.getName());
        actionRepository.deleteByScenarioId(scenario.getId());
        conditionRepository.deleteByScenarioId(scenario.getId());
        addActions(scenario, scenarioAddedEventAvro);
        addConditions(scenario, scenarioAddedEventAvro);
        log.info("Scenario updated {}", scenario);
    }

    public void addScenario(String hubId, ScenarioAddedEventAvro scenarioAddedEventAvro) {
        log.info("Start to add scenario {}", scenarioAddedEventAvro);
        Scenario scenario = new Scenario();
        scenario.setHubId(hubId);
        scenario.setName(scenarioAddedEventAvro.getName());
        scenarioRepository.save(scenario);
        log.info("Scenario added without actions and conditions {}",
                scenarioRepository.findByHubIdAndName(hubId, scenario.getName()));
        addActions(scenario, scenarioAddedEventAvro);
        addConditions(scenario, scenarioAddedEventAvro);
        log.info("Scenario added {}", scenario);
    }

    public void addActions(Scenario scenario, ScenarioAddedEventAvro scenarioAddedEventAvro) {
        List<DeviceActionAvro> actionsAvro = scenarioAddedEventAvro.getActions();
        List<String> sensorIds = actionsAvro.stream()
                .map(DeviceActionAvro::getSensorId)
                .toList();
        List<Sensor> sensors = sensorRepository.findAllById(sensorIds);
        checkSensorsAndScenarioHub(scenario, sensors);
        Map<String, Sensor> sensorsMap = sensors.stream()
                .collect(Collectors.toMap(Sensor::getId, sensor -> sensor));
        List<Action> actions = actionsAvro.stream()
                .map(deviceActionAvro -> EntityMapper.actionAvroToEntity(scenario, deviceActionAvro,
                        sensorsMap.get(deviceActionAvro.getSensorId())))
                .toList();
        actionRepository.saveAll(actions);
        log.info("Actions for scenario {} added", scenario.getName());
    }

    public void addConditions(Scenario scenario, ScenarioAddedEventAvro scenarioAddedEventAvro) {
        List<ScenarioConditionAvro> conditionsAvro = scenarioAddedEventAvro.getConditions();
        List<String> sensorIds = conditionsAvro.stream()
                .map(ScenarioConditionAvro::getSensorId)
                .toList();
        List<Sensor> sensors = sensorRepository.findAllById(sensorIds);
        checkSensorsAndScenarioHub(scenario, sensors);
        Map<String, Sensor> sensorsMap = sensors.stream()
                .collect(Collectors.toMap(Sensor::getId, sensor -> sensor));
        List<Condition> conditions = conditionsAvro.stream()
                .map(scenarioConditionAvro -> {
                    return EntityMapper.conditionAvroToEntity(scenario, scenarioConditionAvro,
                            sensorsMap.get(scenarioConditionAvro.getSensorId()));
                })
                .toList();
        conditionRepository.saveAll(conditions);
        log.info("Conditions for scenario {} added", scenario.getName());
    }

    private void checkSensorsAndScenarioHub(Scenario scenario, List<Sensor> sensors) {
        boolean hasDifferentHub = sensors.stream()
                .map(Sensor::getHubId)
                .anyMatch(hubId -> !hubId.equals(scenario.getHubId()));
        if (hasDifferentHub) {
            throw new IllegalArgumentException("All sensors must belong to the same hub as the scenario: "
                    + scenario.getHubId());
        }
    }
}
