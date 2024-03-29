package com.chriniko.searchadsservice.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class AdIncludedInSearchProcessEvent extends AdEvent {

    public AdIncludedInSearchProcessEvent(String adId) {
        super(adId);
    }

    // Note: for deserialization.
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public AdIncludedInSearchProcessEvent(@JsonProperty("adId") String adId,
                                          @JsonProperty("eventId") String eventId,
                                          @JsonProperty("createdAt") Instant createdAt) {
        super(adId, eventId, createdAt);
    }
}
