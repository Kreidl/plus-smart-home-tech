package ru.yandex.practicum.config;

import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.deserializer.SensorEventDeserializer;
import ru.yandex.practicum.kafka.serializer.GeneralAvroSerializer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.util.Map;
import java.util.Properties;

@Slf4j
@Setter
@ToString
@Component
@Configuration
@ConfigurationProperties("aggregator.kafka")
public class KafkaConfig {
    private Map<String, String> topics;
    private Properties producerProperties;
    private Properties consumerProperties;

    public Properties getProducerProperties() {
        if (producerProperties == null) {
            producerProperties = new Properties();
            producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                    GeneralAvroSerializer.class);
        }
        return producerProperties;
    }

    public Properties getConsumerProperties() {
        if (consumerProperties == null) {
            consumerProperties = new Properties();
            consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "aggregator-group");
            consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorEventDeserializer.class);
            consumerProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        }
        return consumerProperties;
    }

    public String getSensorEventTopic() {
        return topics != null ? topics.get("sensors-events") : "telemetry.sensors.v1";
    }

    public String getSnapshotTopic() {
        return topics != null ? topics.get("snapshots") : "telemetry.snapshots.v1";
    }

    @Bean
    public String sensorEventTopic() {
        return getSensorEventTopic();
    }

    @Bean
    public String snapshotTopic() { return getSnapshotTopic(); }

    @Bean
    public KafkaConsumer<String, SensorEventAvro> kafkaSensorEventConsumer() {
        return new KafkaConsumer<>(getConsumerProperties());
    }

    @Bean
    public KafkaProducer<String, SpecificRecordBase> kafkaSnapshotProducer() {
        return new KafkaProducer<>(getProducerProperties());
    }
}
