package com.chriniko.searchadsservice.serde;

import com.chriniko.searchadsservice.error.ServiceProcessingException;
import com.chriniko.searchadsservice.event.AdEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.common.serialization.Deserializer;

@Log4j2
public class AdEventDeserializer implements Deserializer<AdEvent> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public AdEvent deserialize(String topic, byte[] data) {
        try {
            return  mapper.readValue(data, AdEvent.class);
        } catch (Exception error) {
            String message = "could not deserialize ad event, message: " + error.getMessage();
            log.error(message, error);
            throw new ServiceProcessingException(message, error);
        }
    }
}
