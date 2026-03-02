package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.collector.configuration.KafkaProducerConfig;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEventType;

@Slf4j
@AllArgsConstructor
public abstract class BaseHubEventHandler<T> implements HubEventHandler {
    private final KafkaProducerConfig kafkaProducerConfig;
    KafkaProducer<String, HubEventAvro> kafkaProducer;

    public BaseHubEventHandler(KafkaProducerConfig kafkaProducerConfig) {
        this.kafkaProducerConfig = kafkaProducerConfig;
        kafkaProducer = kafkaProducerConfig.kafkaHubProducer();
    }

    @Override
    public abstract HubEventType getMessageType();

    public abstract T mapToAvro(HubEvent hubEvent);

    @Override
    public void handle(HubEvent hubEvent) {
        T eventAvro = mapToAvro(hubEvent);
        HubEventAvro hubEventAvro = HubEventAvro.newBuilder()
                .setHubId(hubEvent.getHubId())
                .setTimestamp(hubEvent.getTimestamp())
                .setPayload(eventAvro)
                .build();
        ProducerRecord<String, HubEventAvro> record = new ProducerRecord<>(kafkaProducerConfig.hubTopic(),
                hubEvent.getHubId(), hubEventAvro);
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