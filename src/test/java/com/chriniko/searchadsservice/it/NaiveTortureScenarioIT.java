package com.chriniko.searchadsservice.it;


import com.chriniko.searchadsservice.SearchAdsServiceApplication;
import com.chriniko.searchadsservice.client.ClientAdsSimulator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/*

 Important Note: the best would be to use JMH for benchmark/torture test.

 */

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = SearchAdsServiceApplication.class,
        properties = {"application.properties"}
)

@ExtendWith(SpringExtension.class)
public class NaiveTortureScenarioIT {

    @LocalServerPort
    private int port;

    @Autowired
    private RestTemplate restTemplate;

//    @Test
//    public void test() {
//
//        // given
//        int noOfClients = 200;
//        ExecutorService workerPool = Executors.newFixedThreadPool(noOfClients);
//
//        String serviceUrl = "http://localhost:" + port;
//
//        Phaser phaser = new Phaser(noOfClients + 1 /*for the main thread*/);
//
//        // when
//        List<CompletableFuture<Void>> clientSimulations = IntStream.rangeClosed(1, noOfClients)
//                .mapToObj(idx -> new ClientAdsSimulator(serviceUrl, "search something " + idx))
//                .map(clientAdsSimulator -> CompletableFuture.runAsync(() -> {
//
//                            System.out.println(Thread.currentThread().getName() + " --- client simulator just arrived and awaiting signal for work...");
//                            phaser.arriveAndAwaitAdvance(); // Note: workers rendezvous.
//
//                            FullScenarioIT.simulateClientAction(serviceUrl, clientAdsSimulator, restTemplate);
//
//                        }, workerPool)
//                )
//                .collect(Collectors.toList());
//
//
//        phaser.arriveAndDeregister(); // Note: main thread signals workers to start...
//
//
//        // then
//        clientSimulations.forEach(simulation -> {
//            try {
//                simulation.get(40, TimeUnit.SECONDS);
//            } catch (InterruptedException | ExecutionException | TimeoutException e) {
//                throw new RuntimeException(e);
//            }
//        });
//
//
//        // cleanup
//        workerPool.shutdown();
//
//    }


}
