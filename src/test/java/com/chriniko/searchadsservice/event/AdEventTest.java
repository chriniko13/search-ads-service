package com.chriniko.searchadsservice.event;

import com.chriniko.searchadsservice.domain.AdClickSource;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AdEventTest {


    @Test
    void subtypeHandlingWorksAsExpected() throws Exception {

        // given
        String adId = UUID.randomUUID().toString();
        ObjectMapper mapper = new ObjectMapper();



        // when
        String adClickedEventSerialized = mapper.writeValueAsString(new AdClickedEvent(adId));

        // then
        JsonNode eventTypeJson = mapper.readTree(adClickedEventSerialized).get("event-type");
        assertEquals("clicked", eventTypeJson.asText());
        assertEquals(AdClickedEvent.class, mapper.readValue(adClickedEventSerialized, AdEvent.class).getClass());


        // when
        String adClickedEventSerializedMail = mapper.writeValueAsString(new AdClickedEvent(adId, AdClickSource.MAIL_CAMPAIGN));

        // then
        JsonNode adClickedEventSerializerMailJsonNode = mapper.readTree(adClickedEventSerializedMail);

        eventTypeJson = adClickedEventSerializerMailJsonNode.get("event-type");
        assertEquals("clicked", eventTypeJson.asText());

        assertEquals("MAIL_CAMPAIGN", adClickedEventSerializerMailJsonNode.get("source").asText());

        assertEquals(AdClickedEvent.class, mapper.readValue(adClickedEventSerializedMail, AdEvent.class).getClass());



        // when
        String adIncludedInSearchProcessEventSerialized = mapper.writeValueAsString(new AdIncludedInSearchProcessEvent(adId));

        // then
        eventTypeJson = mapper.readTree(adIncludedInSearchProcessEventSerialized).get("event-type");
        assertEquals("included", eventTypeJson.asText());
        assertEquals(AdIncludedInSearchProcessEvent.class, mapper.readValue(adIncludedInSearchProcessEventSerialized, AdEvent.class).getClass());



        // when
        String adAppearedOnSearchResultEventSerialized = mapper.writeValueAsString(new AdAppearedOnSearchResultEvent(adId));

        // then
        eventTypeJson = mapper.readTree(adAppearedOnSearchResultEventSerialized).get("event-type");
        assertEquals("appeared", eventTypeJson.asText());
        assertEquals(AdAppearedOnSearchResultEvent.class, mapper.readValue(adAppearedOnSearchResultEventSerialized, AdEvent.class).getClass());

    }

}