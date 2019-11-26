package com.chriniko.searchadsservice.event;

import lombok.Getter;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Getter
public abstract class AdEvent {

    protected final String eventId;
    protected final Instant createdAt;

    protected final String adId;

    protected AdEvent(String adId) {
        this.adId = adId;
        this.eventId = UUID.randomUUID().toString();
        this.createdAt = Instant.now(Clock.systemUTC());
    }
}
