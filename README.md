### Search Ads Service


                          _                 _
                         | |               | |
  ___  ___  __ _ _ __ ___| |__     __ _  __| |___
 / __|/ _ \/ _` | '__/ __| '_ \   / _` |/ _` / __|
 \__ \  __/ (_| | | | (__| | | | | (_| | (_| \__ \
 |___/\___|\__,_|_|  \___|_| |_|  \__,_|\__,_|___/
                     (_)
  ___  ___ _ ____   ___  ___ ___
 / __|/ _ \ '__\ \ / / |/ __/ _ \
 \__ \  __/ |   \ V /| | (_|  __/
 |___/\___|_|    \_/ |_|\___\___|





##### Assignee: Nikolaos Christidis (nick.christidis@yahoo.com)


#### Mandatory Dependencies in order to run and test service

* Docker & Docker Compose, for installation search web.

* Kafka
    * Execute: `docker-compose up` in order to start 1 `Zookeeper instance` with 3 `Kafka Brokers`.
    
    * (Optional) Execute: `docker-compose down` in order to shutdown the above mentioned.
    
    * Then download kafka in order to have kafka operational scripts, such as: `./kafka-topics.sh <blabla>`, `./kafka-console-consumer <blabla>`, `./kafka-console-producer <blabla>`
    
    * Now create `ad_events` kafka topic with the following command: `./kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 3 --partitions 10 --topic ad_events`
      we can use replication factor 3, due to the fact that we have 3 kafka brokers, also the number of kafka consumers which will consume from this topic can be set in: 
      `KafkaAdEventConsumerCoordinator.NO_OF_CONSUMERS` and we are done, we do not need to write logic because we use `subscribe` instead of `assign` mechanism


#### How to run service
* Two options:
    * Execute: 
        * `mvn clean install -DskipUTs=true -DskipITs`
        * `java -jar -Dspring.profiles.active=dev -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector target/search-ads-service-0.0.1-SNAPSHOT.jar`
                
    * Execute:
        * `mvn spring-boot:run -Dspring.profiles.active=dev -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector`
        

#### Execute Unit Tests
* Execute: `mvn clean test`


#### Execute Integration Tests
* Execute: `mvn clean integration-test -DskipUTs=true` or `mvn clean verify -DskipUTs=true`


#### Test Coverage (via JaCoCo)
* In order to generate reports execute: `mvn clean verify`
    * In order to see unit test coverage open with browser: `target/site/jacoco-ut/index.html`
    * In order to see integration test coverage open with browser: `target/site/jacoco-it/index.html`
    