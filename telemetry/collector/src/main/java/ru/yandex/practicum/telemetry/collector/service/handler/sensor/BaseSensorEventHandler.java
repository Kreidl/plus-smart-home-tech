package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.configuration.KafkaProducerConfig;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEventType;

import java.time.Instant;

@Slf4j
@AllArgsConstructor
public abstract class BaseSensorEventHandler<T> implements SensorEventHandler {
    private final KafkaProducerConfig kafkaProducerConfig;
    private KafkaProducer<String, SensorEventAvro> kafkaProducer;

    public BaseSensorEventHandler(KafkaProducerConfig kafkaProducerConfig) {
        this.kafkaProducerConfig = kafkaProducerConfig;
        kafkaProducer = kafkaProducerConfig.createKafkaSensorProducer();
    }

    @Override
    public abstract SensorEventType getMessageType();

    @Override
    public abstract SensorEventProto.PayloadCase getMessageTypeProto();

    public abstract T protoToAvro(SensorEventProto sensorEvent);

    @Override
    public void handle(SensorEventProto sensorEventProto) {
        log.info("Start of converting SensorEventProto with MessageType {} to Avro",
                getMessageTypeProto());
        T eventAvro = protoToAvro(sensorEventProto);
        Instant timestamp = Instant.ofEpochSecond(sensorEventProto.getTimestamp().getSeconds(),
                sensorEventProto.getTimestamp().getNanos());
        SensorEventAvro sensorEventAvro = SensorEventAvro.newBuilder()
                .setId(sensorEventProto.getId())
                .setHubId(sensorEventProto.getHubId())
                .setTimestamp(timestamp)
                .setPayload(eventAvro)
                .build();
        log.info("End of converting SensorEventProto with MessageType {} to Avro {}",
                getMessageTypeProto(), sensorEventAvro);
        ProducerRecord<String, SensorEventAvro> record = new ProducerRecord<>(kafkaProducerConfig.getSensorTopic(),
                sensorEventProto.getHubId(), sensorEventAvro);
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