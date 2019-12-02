package com.chriniko.searchadsservice.service;

import com.chriniko.searchadsservice.domain.SearchResult;
import com.chriniko.searchadsservice.dto.SearchAdResponse;
import com.chriniko.searchadsservice.error.InvalidAdIdException;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class AdSearchServiceTest {

    @Test
    void process_works_as_expected_visitor_scrolling_case() {

        // given
        String searchText = "ducati panigale";

        AdSearchService service = new AdSearchService(getMockedUserSessionService());

        String sessionId = "someSessionId";

        int offset = 1;
        int size = 10;

        SearchResult searchResult = service.process(sessionId, null, offset, size, searchText);
        String searchId = searchResult.getSearchAdResponse().getSearchId();


        // when
        SearchResult result = service.process(sessionId, searchId, offset, size, searchText);


        // then
        assertNotNull(result);

        SearchAdResponse searchAdResponse = result.getSearchAdResponse();

        assertTrue(searchAdResponse.getNumberOfResults() > 0);

        assertNotNull(service.getSearchesBySearchId().get(searchAdResponse.getSearchId()));

        ConcurrentHashMap.KeySetView<String, Boolean> generatedAdIds = service.getGeneratedAdIds();
        assertEquals(searchAdResponse.getNumberOfResults(), generatedAdIds.size());
    }


    //TODO init visit

    //TODO visitor search request

    @Test
    void isValidAdId() {

        // given
        AdSearchService service = new AdSearchService(getMockedUserSessionService());


        // when
        try {
            service.isValidAdId("foobar");
            fail();
        } catch (InvalidAdIdException error) { // then
            assertTrue(error.getMessage().contains("foobar"));
        }

    }

    private UserSessionService getMockedUserSessionService() {
        return new UserSessionService() {
            @Override
            public String generate() {
                return "foobar";
            }
        };
    }
}