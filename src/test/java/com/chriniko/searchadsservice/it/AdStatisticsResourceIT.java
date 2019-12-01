package com.chriniko.searchadsservice.it;

import com.chriniko.searchadsservice.SearchAdsServiceApplication;
import com.chriniko.searchadsservice.domain.AdStatistics;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = SearchAdsServiceApplication.class,
        properties = {"application.properties"}
)

@ExtendWith(SpringExtension.class)

public class AdStatisticsResourceIT {

    @LocalServerPort
    private int port;

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void get_statistics_for_invalid_ad_id() {

        // given
        String serviceUrl = "http://localhost:" + port;
        String adId = "invalidAdId";

        // when
        try {
            restTemplate.exchange(
                    serviceUrl + "/ad-statistics/" + adId,
                    HttpMethod.GET,
                    null,
                    AdStatistics.class
            );
            fail();
        } catch (HttpClientErrorException e) {

            // then
            assertNotNull(e);
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertEquals("400 Bad Request", e.getMessage());
            assertEquals("the provided ad id: invalidAdId is not valid", e.getResponseBodyAsString());
        }


    }

}
