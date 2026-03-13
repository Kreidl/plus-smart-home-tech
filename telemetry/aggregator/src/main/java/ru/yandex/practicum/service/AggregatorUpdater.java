package ru.yandex.practicum.service;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
@ToString
@Component
public class AggregatorUpdater {
    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        SensorsSnapshotAvro snapshotAvro = snapshots.computeIfAbsent(event.getHubId(), k ->
                SensorsSnapshotAvro.newBuilder()
                        .setHubId(event.getHubId())
                        .setSensorsState(new HashMap<>())
                        .setTimestamp(Instant.now())
                        .build());
        Map<String, SensorStateAvro> sensorStateAvroMap = snapshotAvro.getSensorsState();
        if (sensorStateAvroMap != null && sensorStateAvroMap.containsKey(event.getId())) {
            SensorStateAvro oldState = sensorStateAvroMap.get(event.getId());
            if (oldState.getTimestamp().isAfter(event.getTimestamp())
                    || oldState.getData().equals(event.getPayload())) {
                return Optional.empty();
            }
        }
        SensorStateAvro sensorStateAvro = SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();
        sensorStateAvroMap.put(event.getId(), sensorStateAvro);
        snapshotAvro.setSensorsState(sensorStateAvroMap);
        snapshotAvro.setTimestamp(event.getTimestamp());
        return Optional.of(snapshotAvro);
    }
}
