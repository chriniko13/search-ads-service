package com.chriniko.searchadsservice.it;

import com.chriniko.searchadsservice.SearchAdsServiceApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = SearchAdsServiceApplication.class,
        properties = {"application.properties"}
)

@ExtendWith(SpringExtension.class)

public class FullScenarioIT {

    @LocalServerPort
    private int port;


    @Test
    public void main_func_works_as_expected() {


        assertEquals(1, 2);
    }

}
