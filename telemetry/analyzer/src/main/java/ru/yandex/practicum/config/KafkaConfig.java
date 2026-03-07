package ru.yandex.practicum.config;

import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.deserializer.HubEventDeserializer;
import ru.yandex.practicum.kafka.deserializer.SensorSnapshotDeserializer;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Map;
import java.util.Properties;

@Slf4j
@Setter
@ToString
@Component
@Configuration
@ConfigurationProperties("analyzer.kafka")
public class KafkaConfig {
    private Map<String, String> topics;
    private Properties hubConsumerProperties;
    private Properties snapshotConsumerProperties;

    public Properties getHubConsumerProperties() {
        if (hubConsumerProperties == null) {
            hubConsumerProperties = new Properties();
            hubConsumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "analyzer-snapshot");
            hubConsumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            hubConsumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            hubConsumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            hubConsumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                    HubEventDeserializer.class);
        }
        return hubConsumerProperties;
    }

    public Properties getSnapshotConsumerProperties() {
        if (snapshotConsumerProperties == null) {
            snapshotConsumerProperties = new Properties();
            snapshotConsumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "snapshot-processor");
            snapshotConsumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            snapshotConsumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            snapshotConsumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            snapshotConsumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                    SensorSnapshotDeserializer.class);
        }
        return snapshotConsumerProperties;
    }

    public String getHubEventTopic() {
        return topics != null ? topics.get("hub") : "telemetry.hubs.v1";
    }

    public String getSnapshotTopic() {
        return topics != null ? topics.get("snapshot") : "telemetry.snapshots.v1";
    }

    public KafkaConsumer<String, HubEventAvro> createKafkaHubEventConsumer() {
        return new KafkaConsumer<>(getHubConsumerProperties());
    }

    public KafkaConsumer<String, SensorsSnapshotAvro> createKafkaSnapshotConsumer() {
        return new KafkaConsumer<>(getSnapshotConsumerProperties());
    }
}
