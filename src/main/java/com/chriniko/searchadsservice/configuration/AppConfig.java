package com.chriniko.searchadsservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class AppConfig {


    @Bean
    public ExecutorService workerPool() {
        return new ThreadPoolExecutor(

                30, 60,
                60L, TimeUnit.SECONDS,

                new SynchronousQueue<>(), // Note: if no threads available, block the caller until a thread becomes available.

                new ThreadFactory() {
                    private AtomicInteger workerIdx = new AtomicInteger();

                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r);
                        t.setName("worker-" + workerIdx.getAndIncrement());
                        return t;
                    }
                }
        );
    }

}
