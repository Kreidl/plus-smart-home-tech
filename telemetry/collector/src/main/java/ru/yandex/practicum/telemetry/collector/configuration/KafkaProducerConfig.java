package ru.yandex.practicum.telemetry.collector.configuration;

import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Setter
@ToString
@Configuration
@ConfigurationProperties("collector.kafka")
public class KafkaProducerConfig {

    private Map<String, String> topics;
    private Map<String, String> producer = new HashMap<>();

    public Properties getProducerProperties() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "ru.yandex.practicum.kafka.telemetry.collector.configuration.AvroSerializer");
        properties.putAll(producer);
        return properties;
    }

    public String getHubTopic() {
        return topics != null ? topics.get("hubs-events") : "telemetry.hubs.v1";
    }

    public String getSensorTopic() {
        return topics != null ? topics.get("sensors-events") : "telemetry.sensors.v1";
    }

    @Bean
    public String hubTopic() {
        return getHubTopic();
    }

    @Bean
    public String sensorTopic() {
        return getSensorTopic();
    }

    @Bean
    public KafkaProducer<String, HubEventAvro> kafkaHubProducer() {
        return new KafkaProducer<>(getProducerProperties());
    }

    @Bean
    public KafkaProducer<String, SensorEventAvro> kafkaSensorProducer() {
        return new KafkaProducer<>(getProducerProperties());
    }
}