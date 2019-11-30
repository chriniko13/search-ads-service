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

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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
        String serviceUrl = "http://localhost:" + port;

        // when
        ClientAdsSimulator clientAdsSimulator = new ClientAdsSimulator(serviceUrl, "apartments for rent, Athens");
        simulateClientAction(serviceUrl, clientAdsSimulator, restTemplate);

    }

    private static ThreadMXBean threadMXBean;

    static {
        threadMXBean = ManagementFactory.getThreadMXBean();

        if (!threadMXBean.isThreadCpuTimeSupported())
            throw new IllegalStateException("CPU time not supported on this platform");

        threadMXBean.setThreadCpuTimeEnabled(true);
    }

    public static void simulateClientAction(String serviceUrl, ClientAdsSimulator clientAdsSimulator, RestTemplate restTemplate) {

        System.out.println(Thread.currentThread().getName() + " --- client simulator just fired...");

        long startThreadCpuTime = threadMXBean.getCurrentThreadCpuTime();
        long startThreadUserTime = threadMXBean.getCurrentThreadUserTime();

        try {

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
                    assertEquals(0, adStatistics.getClickedCountFromSearch());
                    assertEquals(0, adStatistics.getClickedCountFromCampaign());
                }
            });


            // when - then (all ads except first/displayed page, have been included only NOT displayed).
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
                        assertEquals(0, adStatistics.getClickedCountFromSearch());
                        assertEquals(0, adStatistics.getClickedCountFromCampaign());
                    }
                }
            });


            // when - then (let's click on some ads)
            int randomPageNumber = ThreadLocalRandom.current().nextInt(0, clientAdsSimulator.getPagedAds().getTotalPages());
            List<Ad> randomAdsSelectedForClicking = clientAdsSimulator.getPagedAds().getAds().get(randomPageNumber);

            final Set<String> adsClickedFromCampaign = new LinkedHashSet<>();

            randomAdsSelectedForClicking.stream().map(Ad::getId).forEach(id -> {

                boolean fromCampaign = ThreadLocalRandom.current().nextInt(0, 2) == 1;
                if (fromCampaign) {
                    adsClickedFromCampaign.add(id);
                }
                clientAdsSimulator.clicked(id, fromCampaign);
            });

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

                    if (adsClickedFromCampaign.contains(adId)) {
                        assertEquals(1, adStatistics.getClickedCountFromCampaign());
                    } else {
                        assertEquals(1, adStatistics.getClickedCountFromSearch());
                    }

                }
            });


            // when - then (move to next page, recheck statistics)
            clientAdsSimulator.proceedToNextPage();
            List<Ad> currentDisplayedAdsNextPage = clientAdsSimulator.getCurrentDisplayedAds();

            Awaitility.await().atMost(30, TimeUnit.SECONDS).untilAsserted(() -> {

                for (Ad notPageDisplayedAd : currentDisplayedAdsNextPage) {

                    String adId = notPageDisplayedAd.getId();

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
                }


            });


            // when - then (move to previous page, recheck statistics, counter of appeared on search and included in search should be the same)
            clientAdsSimulator.proceedToPreviousPage();
            List<Ad> currentDisplayedAdsFromPreviousPage = clientAdsSimulator.getCurrentDisplayedAds();
            Awaitility.await().atMost(30, TimeUnit.SECONDS).untilAsserted(() -> {

                for (Ad notPageDisplayedAd : currentDisplayedAdsFromPreviousPage) {

                    String adId = notPageDisplayedAd.getId();

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
                }

            });

        } finally {

            long totalThreadCpuTime = threadMXBean.getCurrentThreadCpuTime() - startThreadCpuTime;
            long totalThreadUserTime = threadMXBean.getCurrentThreadUserTime() - startThreadUserTime;

            System.out.println("\n\n~~~~~~~~~~~~~~~~~~~~" + Thread.currentThread().getName());
            System.out.println("total thread cpu time (ms): " + TimeUnit.MILLISECONDS.convert(totalThreadCpuTime, TimeUnit.NANOSECONDS));
            System.out.println("total thread user time (ms): " + TimeUnit.MILLISECONDS.convert(totalThreadUserTime, TimeUnit.NANOSECONDS));

        }
    }

}
