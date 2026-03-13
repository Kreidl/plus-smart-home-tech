package ru.yandex.practicum.service.processors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.KafkaConfig;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.service.processors.handlers.snapshot.SnapshotHandler;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor {
    private final KafkaConfig kafkaConfig;
    private final SnapshotHandler snapshotHandler;
    private final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);

    public void start() {
        KafkaConsumer<String, SensorsSnapshotAvro> consumer = kafkaConfig.createKafkaSnapshotConsumer();
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        try {
            consumer.subscribe(List.of(kafkaConfig.getSnapshotTopic()));
            while (true) {
                log.info("Start reading records");
                ConsumerRecords<String, SensorsSnapshotAvro> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);
                log.info("Start reading records {}", records);
                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    SensorsSnapshotAvro sensorsSnapshotAvro = record.value();
                    log.info("Start reading record {}", sensorsSnapshotAvro);
                    snapshotHandler.handle(sensorsSnapshotAvro);
                    log.info("End reading record value {}", sensorsSnapshotAvro);
                }
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Error of reading data");
        } finally {
            try {
                consumer.commitSync();
            } finally {
                consumer.close();
                log.info("Consumer closed");
            }
        }
    }
}
