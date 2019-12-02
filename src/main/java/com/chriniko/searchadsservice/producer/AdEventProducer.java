package com.chriniko.searchadsservice.producer;

import com.chriniko.searchadsservice.event.AdEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AdEventProducer {

    CompletableFuture<Void> send(AdEvent event);

    CompletableFuture<Void> send(List<? extends AdEvent> events);

}
