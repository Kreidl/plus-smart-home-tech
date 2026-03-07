package ru.yandex.practicum.service.processors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.KafkaConfig;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.service.processors.handlers.hub.HubEventHandlers;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {
    private final KafkaConfig kafkaConfig;
    private final HubEventHandlers hubEventHandlers;
    private final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);

    @Override
    public void run() {
        KafkaConsumer<String, HubEventAvro> consumer = kafkaConfig.createKafkaHubEventConsumer();
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        try {
            consumer.subscribe(List.of(kafkaConfig.getHubEventTopic()));
            while (true) {
                ConsumerRecords<String, HubEventAvro> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);
                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    HubEventAvro hubEventAvro = record.value();
                    String payload = hubEventAvro.getPayload().getClass().getSimpleName();
                    if (hubEventHandlers.getHandlers().containsKey(payload)) {
                        hubEventHandlers.getHandlers().get(payload).handle(hubEventAvro);
                    }
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
