package com.chriniko.searchadsservice.serde;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.log4j.Log4j2;

import java.time.Instant;

@Log4j2
public class InstantDeserializerSilenced extends JsonDeserializer<Instant> {

    @Override
    public Instant deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
        try {
            String text = jsonParser.getText();
            return Instant.parse(text);
        } catch (Exception error) {
            log.error("instant deserialization error occurred", error);
            return null;
        }
    }
}
