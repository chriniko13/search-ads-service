package com.chriniko.searchadsservice.consumer;

import com.chriniko.searchadsservice.event.AdAppearedOnSearchResultEvent;
import com.chriniko.searchadsservice.event.AdClickedEvent;
import com.chriniko.searchadsservice.event.AdEvent;
import com.chriniko.searchadsservice.event.AdIncludedInSearchProcessEvent;
import com.chriniko.searchadsservice.serde.AdEventDeserializer;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Collections;
import java.util.Properties;

@Log4j2
public class KafkaAdEventConsumer extends AdEventConsumer {

    private final KafkaConsumer<String, AdEvent> consumer;

    public KafkaAdEventConsumer(int no) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "KafkaAdEventConsumer---" + no);

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, AdEventDeserializer.class.getName());

        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 300);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("ad_events"));
    }

    @Override
    public void consume() {

        while (true) {
            ConsumerRecords<String, AdEvent> consumerRecords = consumer.poll(1000);

            final int logDebugAppear = 20;
            int counter = 0;

            // Note: 1000 is the time in milliseconds consumer will wait if no record is found at broker.
            if (consumerRecords.count() == 0) {
                counter++;
                if (counter == logDebugAppear) {
                    log.debug("no ad-events consumed from kafka");
                    counter = 0;
                }
                return;
            }

            for (ConsumerRecord<String, AdEvent> consumerRecord : consumerRecords) {

                String adId = consumerRecord.key();
                AdEvent adEvent = consumerRecord.value();

                if (adEvent instanceof AdAppearedOnSearchResultEvent) {

                    //TODO

                } else if (adEvent instanceof AdClickedEvent) {

                    //TODO

                } else if (adEvent instanceof AdIncludedInSearchProcessEvent) {

                    //TODO

                } else {
                    log.error("not valid ad-event consumed!");
                }

            }

            consumer.commitAsync((offsets, exception) -> {
                if (exception != null) {
                    log.error("could not commit offsets, message: " + exception.getMessage(), exception);
                }
            });
        }

    }
}
