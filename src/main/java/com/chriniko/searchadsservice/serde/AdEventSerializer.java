package com.chriniko.searchadsservice.serde;

import com.chriniko.searchadsservice.error.ServiceProcessingException;
import com.chriniko.searchadsservice.event.AdEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.common.serialization.Serializer;

@Log4j2
public class AdEventSerializer implements Serializer<AdEvent> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, AdEvent event) {

        try {
            return mapper.writeValueAsBytes(event);
        } catch (Exception error) {
            String message = "could not serialize ad event, message: " + error.getMessage();
            log.error(message, error);
            throw new ServiceProcessingException(message, error);
        }
    }
}
