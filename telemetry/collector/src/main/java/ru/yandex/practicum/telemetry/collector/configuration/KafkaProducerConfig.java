package ru.yandex.practicum.telemetry.collector.configuration;

import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.util.Map;
import java.util.Properties;

@Slf4j
@Setter
@ToString
@Configuration
@ConfigurationProperties("collector.kafka")
public class KafkaProducerConfig {
    private Map<String, String> topics;
    private Map<String, String> hubProducerProperties;
    private Map<String, String> sensorProducerProperties;

    public Properties getHubProducerProperties() {
        Properties props = new Properties();
        props.putAll(hubProducerProperties);
        return props;
    }

    public Properties getSensorProducerProperties() {
        Properties props = new Properties();
        props.putAll(sensorProducerProperties);
        return props;
    }

    public String getHubTopic() {
        return topics != null ? topics.get("hubs-events") : "telemetry.hubs.v1";
    }

    public String getSensorTopic() {
        return topics != null ? topics.get("sensors-events") : "telemetry.sensors.v1";
    }

    public KafkaProducer<String, HubEventAvro> createKafkaHubProducer() {
        return new KafkaProducer<>(getHubProducerProperties());
    }

    public KafkaProducer<String, SensorEventAvro> createKafkaSensorProducer() {
        return new KafkaProducer<>(getSensorProducerProperties());
    }
}