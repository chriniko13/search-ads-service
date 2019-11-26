package com.chriniko.searchadsservice.consumer;

public abstract class AdEventConsumer extends Thread {

    public abstract void consume();

    @Override
    public void run() {
        consume();
    }
}
