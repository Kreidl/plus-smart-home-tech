package ru.yandex.practicum.config;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.util.Map;
import java.util.Properties;

@Slf4j
@Setter
@ToString
@Component
@Configuration
@AllArgsConstructor
@ConfigurationProperties("aggregator.kafka")
public class KafkaConfig {
    private Map<String, String> topics;
    private Map<String, String> producerProperties;
    private Map<String, String> consumerProperties;

    public Properties getProducerProperties() {
        Properties props = new Properties();
        props.putAll(producerProperties);
        return props;
    }

    public Properties getConsumerProperties() {
        Properties props = new Properties();
        props.putAll(consumerProperties);
        return props;
    }

    public String getSensorEventTopic() {
        return topics != null ? topics.get("sensors-events") : "telemetry.sensors.v1";
    }

    public String getSnapshotTopic() {
        return topics != null ? topics.get("snapshots") : "telemetry.snapshots.v1";
    }

    public KafkaConsumer<String, SensorEventAvro> createKafkaSensorEventConsumer() {
        return new KafkaConsumer<>(getConsumerProperties());
    }

    public KafkaProducer<String, SpecificRecordBase> createKafkaSnapshotProducer() {
        return new KafkaProducer<>(getProducerProperties());
    }
}
