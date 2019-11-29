package com.chriniko.searchadsservice.consumer;

import com.chriniko.searchadsservice.event.AdEvent;
import com.chriniko.searchadsservice.serde.AdEventDeserializer;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.function.Consumer;

/*
    Note: this kafka consumer consumes messages using dynamic partition assignment and consumer group coordination, with the usage
          of subscribe and GROUP_ID_CONFIG (mandatory for group management functionality)
 */

@Log4j2
public class KafkaAdEventConsumer extends AdEventConsumer {

    private final KafkaConsumer<String, AdEvent> consumer;

    private final Consumer<ConsumerRecord<String, AdEvent>> adEventProcessing;

    public KafkaAdEventConsumer(String groupIdSuffix, Consumer<ConsumerRecord<String, AdEvent>> adEventProcessing) {
        this.adEventProcessing = adEventProcessing;
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "KafkaAdEventConsumer---" + groupIdSuffix);

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, AdEventDeserializer.class.getName());

        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 700);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("ad_events"));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.debug("shutting down kafka ad-events consumer...");
            consumer.wakeup();
        }));
    }

    @Override
    public void consume() {

        try {
            while (true) {
                ConsumerRecords<String, AdEvent> consumerRecords = consumer.poll(Duration.ofMillis(1000));

                final int logDebugAppear = 20;
                int counter = 0;

                if (consumerRecords.count() == 0) {
                    counter++;
                    if (counter == logDebugAppear) {
                        log.debug("no ad-events consumed from kafka");
                        counter = 0;
                    }
                    continue;
                }

                for (ConsumerRecord<String, AdEvent> consumerRecord : consumerRecords) {
                    adEventProcessing.accept(consumerRecord);
                }

                consumer.commitAsync((offsets, exception) -> {
                    if (exception != null) {
                        log.error("could not commit offsets, message: " + exception.getMessage(), exception);
                    }
                });
            }

        } catch (WakeupException error) {
            // Note: do nothing, we are shutting down, exception thrown in order to exit loop.
        } catch (Exception e) {
            log.error("unknown exception during consumption occurred, message: " + e.getMessage(), e);
        } finally {
            consumer.close();
        }

    }
}
