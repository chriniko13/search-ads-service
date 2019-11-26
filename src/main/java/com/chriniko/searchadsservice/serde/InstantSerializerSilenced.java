package com.chriniko.searchadsservice.serde;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.time.Instant;

@Log4j2
public class InstantSerializerSilenced extends JsonSerializer<Instant> {

    @Override
    public void serialize(Instant value, JsonGenerator gen, SerializerProvider serializers) throws IOException {

        try {
            String s = value.toString();
            gen.writeString(s);
        } catch (Exception error) {
            log.error("instant serialization error occurred", error);
            gen.writeNull();
        }
    }
}
