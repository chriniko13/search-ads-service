package com.chriniko.searchadsservice.event;

import com.chriniko.searchadsservice.serde.InstantDeserializerSilenced;
import com.chriniko.searchadsservice.serde.InstantSerializerSilenced;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "event-type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AdIncludedInSearchProcessEvent.class, name = "included"),
        @JsonSubTypes.Type(value = AdAppearedOnSearchResultEvent.class, name = "appeared"),
        @JsonSubTypes.Type(value = AdClickedEvent.class, name = "clicked")
})

@Getter
public abstract class AdEvent {

    protected final String eventId;

    @JsonDeserialize(using = InstantDeserializerSilenced.class)
    @JsonSerialize(using = InstantSerializerSilenced.class)
    protected final Instant createdAt;

    protected final String adId;

    protected AdEvent(String adId) {
        this.adId = adId;
        this.eventId = UUID.randomUUID().toString();
        this.createdAt = Instant.now(Clock.systemUTC());
    }

    protected AdEvent(String adId, String eventId, Instant createdAt) {
        this.adId = adId;
        this.eventId = eventId;
        this.createdAt = createdAt;
    }

}
