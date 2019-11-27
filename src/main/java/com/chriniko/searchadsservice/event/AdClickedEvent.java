package com.chriniko.searchadsservice.event;

import com.chriniko.searchadsservice.domain.AdClickSource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.Instant;

public class AdClickedEvent extends AdEvent {

    @Getter
    private AdClickSource source;

    public AdClickedEvent(String adId) {
        super(adId);
        source = AdClickSource.SEARCH;
    }

    public AdClickedEvent(String adId, AdClickSource source) {
        super(adId);
        this.source = source;
    }

    // Note: for deserialization.
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public AdClickedEvent(@JsonProperty("adId") String adId,
                          @JsonProperty("eventId") String eventId,
                          @JsonProperty("createdAt") Instant createdAt,
                          @JsonProperty("source") AdClickSource source) {
        super(adId, eventId, createdAt);
        this.source = source;
    }

}
