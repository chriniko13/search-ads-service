package com.chriniko.searchadsservice.consumer;

import com.chriniko.searchadsservice.event.AdAppearedOnSearchResultEvent;
import com.chriniko.searchadsservice.event.AdClickedEvent;
import com.chriniko.searchadsservice.event.AdEvent;
import com.chriniko.searchadsservice.event.AdIncludedInSearchProcessEvent;
import com.chriniko.searchadsservice.service.AdStatisticsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nurkiewicz.typeof.TypeOf;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

@Log4j2
@Component
public class KafkaAdEventConsumerCoordinator {


    /*
        Note: Define the number of consumers which will consume messages within the same consumer group (group management functionality).
              We use thread per consumer technique in order to scale, the maximum scale would be to have thread per topic partition.
     */
    private static final int NO_OF_CONSUMERS = 2;


    private final ExecutorService workerPool;
    private final AdStatisticsService adStatisticsService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public KafkaAdEventConsumerCoordinator(ExecutorService workerPool, AdStatisticsService adStatisticsService) {

        this.workerPool = workerPool;
        this.adStatisticsService = adStatisticsService;

        init();
    }

    private void init() {

        Consumer<ConsumerRecord<String, AdEvent>> adEventProcessing = consumerRecord -> {
            String adId = consumerRecord.key();
            AdEvent adEvent = consumerRecord.value();

            //printConsumedMessage(adEvent); // Note: enable it for debugging purposes.

            TypeOf.whenTypeOf(adEvent)

                    .is(AdAppearedOnSearchResultEvent.class)
                    .then(adAppearedOnSearchResultEvent -> adStatisticsService.appearedOnSearch(adId))

                    .is(AdClickedEvent.class)
                    .then(adClickedEvent -> adStatisticsService.clicked(adId, adClickedEvent.getSource()))

                    .is(AdIncludedInSearchProcessEvent.class).
                    then(adIncludedInSearchProcessEvent -> adStatisticsService.includedInSearch(adId))

                    .orElse(unknownAdEvent -> {
                        log.error("not valid ad-event consumed, event type: " + unknownAdEvent.getClass().getName() + ", event: " + unknownAdEvent);
                    });
        };

        String groupIdSuffix = "Coord1";

        for (int i = 0; i < NO_OF_CONSUMERS; i++) {
            KafkaAdEventConsumer kafkaAdEventConsumer = new KafkaAdEventConsumer(groupIdSuffix, adEventProcessing);
            this.workerPool.submit(kafkaAdEventConsumer);
        }

    }

    private void printConsumedMessage(AdEvent adEvent) {
        try {
            String serializedAdEvent = mapper.writeValueAsString(adEvent);
            log.debug("will consume ad-event: " + serializedAdEvent);
        } catch (JsonProcessingException ignored) {
            log.error("error occurred during serialization of ad-event, message: " + ignored.getMessage(), ignored);
        }

    }

}
