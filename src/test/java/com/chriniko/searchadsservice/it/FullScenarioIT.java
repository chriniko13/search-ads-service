package com.chriniko.searchadsservice.it;

import com.chriniko.searchadsservice.SearchAdsServiceApplication;
import com.chriniko.searchadsservice.client.ClientAdsSimulator;
import com.chriniko.searchadsservice.domain.Ad;
import com.chriniko.searchadsservice.domain.AdStatistics;
import com.chriniko.searchadsservice.domain.PagedAds;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = SearchAdsServiceApplication.class,
        properties = {"application.properties"}
)

@ExtendWith(SpringExtension.class)

public class FullScenarioIT {

    @LocalServerPort
    private int port;

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void main_func_works_as_expected() throws Exception {


        // given
        String serviceUrl = "http://localhost:"+port;


        // when
        ClientAdsSimulator clientAdsSimulator = new ClientAdsSimulator(serviceUrl, "apartments for rent, Athens");


        // then
        List<Ad> currentDisplayedAds = clientAdsSimulator.getCurrentDisplayedAds();
        assertEquals(10, currentDisplayedAds.size());


        // when - then (these ads have been displayed and included).
        Awaitility.await().atMost(30, TimeUnit.SECONDS).untilAsserted(() -> {

            for (Ad currentDisplayedAd : currentDisplayedAds) {
                String adId = currentDisplayedAd.getId();

                ResponseEntity<AdStatistics> adStatisticsResponseEntity = restTemplate.exchange(
                        serviceUrl + "/ad-statistics/" + adId,
                        HttpMethod.GET,
                        null,
                        AdStatistics.class
                );
                AdStatistics adStatistics = adStatisticsResponseEntity.getBody();
                assertNotNull(adStatistics);

                assertEquals(1, adStatistics.getAppearedOnSearchCount());
                assertEquals(1, adStatistics.getIncludedInSearchCount());
                assertEquals(0, adStatistics.getClickedCount());
            }
        });



        // when - then (these ads have been included only not displayed).
        Awaitility.await().atMost(30, TimeUnit.SECONDS).untilAsserted(() -> {

            PagedAds pagedAds = clientAdsSimulator.getPagedAds();
            List<List<Ad>> ads = pagedAds.getAds();
            List<List<Ad>> adsWithNotFirstPage = ads.subList(1, ads.size());

            for (List<Ad> notPageDisplayedAds : adsWithNotFirstPage) {

                for (Ad notPageDisplayedAd : notPageDisplayedAds) {

                    String adId = notPageDisplayedAd.getId();

                    ResponseEntity<AdStatistics> adStatisticsResponseEntity = restTemplate.exchange(
                            serviceUrl + "/ad-statistics/" + adId,
                            HttpMethod.GET,
                            null,
                            AdStatistics.class
                    );
                    AdStatistics adStatistics = adStatisticsResponseEntity.getBody();
                    assertNotNull(adStatistics);

                    assertEquals(0, adStatistics.getAppearedOnSearchCount());
                    assertEquals(1, adStatistics.getIncludedInSearchCount());
                    assertEquals(0, adStatistics.getClickedCount());
                }
            }
        });


        // when - then (let's click on some ads) //TODO click on other type...
        int randomPageNumber = ThreadLocalRandom.current().nextInt(0, clientAdsSimulator.getPagedAds().getTotalPages());
        List<Ad> randomAdsSelectedForClicking = clientAdsSimulator.getPagedAds().getAds().get(randomPageNumber);

        randomAdsSelectedForClicking.stream().map(Ad::getId).forEach(clientAdsSimulator::clicked);

        Awaitility.await().atMost(30, TimeUnit.SECONDS).untilAsserted(() -> {

            for (Ad ad : randomAdsSelectedForClicking) {

                String adId = ad.getId();

                ResponseEntity<AdStatistics> adStatisticsResponseEntity = restTemplate.exchange(
                        serviceUrl + "/ad-statistics/" + adId,
                        HttpMethod.GET,
                        null,
                        AdStatistics.class
                );
                AdStatistics adStatistics = adStatisticsResponseEntity.getBody();
                assertNotNull(adStatistics);

                assertEquals(1, adStatistics.getClickedCount());
            }
        });


        // when - then (move to next page, recheck statistics)
        //TODO..


        // when - then (move to previous page, recheck statistics)
        //TODO..

        assertEquals(1, 2);
    }

}
