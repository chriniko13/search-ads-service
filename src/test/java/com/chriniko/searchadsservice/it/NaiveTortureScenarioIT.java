package com.chriniko.searchadsservice.it;



import com.chriniko.searchadsservice.SearchAdsServiceApplication;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;


/*

 Important Note: the best would be to use JMH for benchmark/torture test, but time flies...
                 so by using this naive torture test and a tool like a VisualVM we can get some insights from the app.


 */

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = SearchAdsServiceApplication.class,
        properties = {"application.properties"}
)

@ExtendWith(SpringExtension.class)
public class NaiveTortureScenarioIT {




}
