package ru.yandex.practicum.config;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Map;
import java.util.Properties;

@Setter
@ToString
@Component
@Configuration
@ConfigurationProperties("analyzer.kafka")
@AllArgsConstructor
public class KafkaConfig {
    private Map<String, String> topics;
    private Map<String, String> hubConsumerProperties;
    private Map<String, String> snapshotConsumerProperties;

    public String getHubEventTopic() {
        return topics != null ? topics.get("hub-consumer-topics") : "telemetry.hubs.v1";
    }

    public String getSnapshotTopic() {
        return topics != null ? topics.get("snapshot-consumer-topics") : "telemetry.snapshots.v1";
    }

    public Properties getHubConsumerProperties() {
        Properties props = new Properties();
        props.putAll(hubConsumerProperties);
        return props;
    }

    public Properties getSnapshotConsumerProperties() {
        Properties props = new Properties();
        props.putAll(snapshotConsumerProperties);
        return props;
    }

    public KafkaConsumer<String, HubEventAvro> createKafkaHubEventConsumer() {
        return new KafkaConsumer<>(getHubConsumerProperties());
    }

    public KafkaConsumer<String, SensorsSnapshotAvro> createKafkaSnapshotConsumer() {
        return new KafkaConsumer<>(getSnapshotConsumerProperties());
    }
}
