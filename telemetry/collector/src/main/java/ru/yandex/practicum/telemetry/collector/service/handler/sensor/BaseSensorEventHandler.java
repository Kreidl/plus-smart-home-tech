package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.configuration.KafkaProducerConfig;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEventType;

@Slf4j
@AllArgsConstructor
public abstract class BaseSensorEventHandler<T> implements SensorEventHandler {
    private final KafkaProducerConfig kafkaProducerConfig;
    KafkaProducer<String, SensorEventAvro> kafkaProducer;

    public BaseSensorEventHandler(KafkaProducerConfig kafkaProducerConfig) {
        this.kafkaProducerConfig = kafkaProducerConfig;
        kafkaProducer = kafkaProducerConfig.kafkaSensorProducer();
    }

    @Override
    public abstract SensorEventType getMessageType();

    public abstract T mapToAvro(SensorEvent sensorEvent);

    @Override
    public void handle(SensorEvent sensorEvent) {
        T eventAvro = mapToAvro(sensorEvent);
        SensorEventAvro sensorEventAvro = SensorEventAvro.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(sensorEvent.getTimestamp())
                .setPayload(eventAvro)
                .build();
        ProducerRecord<String, SensorEventAvro> record = new ProducerRecord<>(kafkaProducerConfig.sensorTopic(),
                sensorEvent.getHubId(), sensorEventAvro);
        kafkaProducer.send(record, (metadata, exception) -> {
            if (exception != null) {
                log.error("Kafka send failed", exception);
            } else {
                log.info("Message sent to topic {} partition {} offset {}",
                        metadata.topic(), metadata.partition(), metadata.offset());
            }
        });
        kafkaProducer.flush();
    }
}