package com.chriniko.searchadsservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
public class AdEventTriggerService {

    private final ExecutorService workerPool;

    @Autowired
    public AdEventTriggerService(ExecutorService workerPool) {
        this.workerPool = workerPool;
    }

    public void adClicked(String adId) {
        CompletableFuture.runAsync(() -> {

            //TODO...

        }, workerPool);
    }

    public void adsAppeared(List<String> adIds) {
        CompletableFuture.runAsync(() -> {

            //TODO...

        }, workerPool);
    }

    public void adsIncluded(Set<String> adIds) {
        CompletableFuture.runAsync(() -> {

            //TODO...

        }, workerPool);
    }
}
