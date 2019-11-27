package com.chriniko.searchadsservice.producer;

import com.chriniko.searchadsservice.event.AdEvent;
import com.chriniko.searchadsservice.serde.AdEventSerializer;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.concurrent.CompletableFuture;

@Log4j2
@Component
public class KafkaAdEventProducer implements AdEventProducer {

    private static final String KAFKA_SERVER_URL = "localhost";
    private static final int KAFKA_SERVER_PORT = 9092;
    private static final String CLIENT_ID = "KafkaAdEventProducer";

    private final KafkaProducer<String, AdEvent> producer;
    private final String topic;

    public KafkaAdEventProducer() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", KAFKA_SERVER_URL + ":" + KAFKA_SERVER_PORT);
        properties.put("client.id", CLIENT_ID);
        properties.put("key.serializer", StringSerializer.class.getName());
        properties.put("value.serializer", AdEventSerializer.class.getName());

        this.producer = new KafkaProducer<>(properties);
        this.topic = "ad_events";

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.debug("shutting down kafka ad-events producer...");
            producer.close();
        }));
    }

    @Override
    public CompletableFuture<Void> send(AdEvent event) {

        CompletableFuture<Void> cf = new CompletableFuture<>();

        String adId = event.getAdId();

        producer.send(
                new ProducerRecord<>(topic, adId, event),
                (recordMetadata, error) -> {
                    if (error != null) {
                        log.error("could not send ad event to kafka, message: " + error, error);
                        cf.completeExceptionally(error);
                    } else {
                        cf.complete(null);
                    }
                });

        return cf;
    }
}
