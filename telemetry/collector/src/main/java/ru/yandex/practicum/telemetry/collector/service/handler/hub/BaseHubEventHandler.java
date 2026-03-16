package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.collector.configuration.KafkaProducerConfig;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEventType;

import java.time.Instant;

@Slf4j
@AllArgsConstructor
public abstract class BaseHubEventHandler<T> implements HubEventHandler {
    private final KafkaProducerConfig kafkaProducerConfig;
    private KafkaProducer<String, HubEventAvro> kafkaProducer;

    public BaseHubEventHandler(KafkaProducerConfig kafkaProducerConfig) {
        this.kafkaProducerConfig = kafkaProducerConfig;
        kafkaProducer = kafkaProducerConfig.createKafkaHubProducer();
    }

    @Override
    public abstract HubEventType getMessageType();

    public abstract T protoToAvro(HubEventProto hubEvent);

    @Override
    public void handle(HubEventProto hubEventProto) {
        log.info("Start of converting HubEventProto with MessageType {} to Avro",
                getMessageTypeProto());
        T eventAvro = protoToAvro(hubEventProto);
        Instant timestamp = Instant.ofEpochSecond(hubEventProto.getTimestamp().getSeconds(),
                hubEventProto.getTimestamp().getNanos());
        HubEventAvro hubEventAvro = HubEventAvro.newBuilder()
                .setHubId(hubEventProto.getHubId())
                .setTimestamp(timestamp)
                .setPayload(eventAvro)
                .build();
        log.info("End of converting HubEventProto with MessageType {} to Avro {}",
                getMessageTypeProto(), hubEventAvro);
        ProducerRecord<String, HubEventAvro> record = new ProducerRecord<>(kafkaProducerConfig.getHubTopic(),
                hubEventProto.getHubId(), hubEventAvro);
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