package com.chriniko.searchadsservice.service;

import com.chriniko.searchadsservice.domain.ClientSearchState;
import com.chriniko.searchadsservice.domain.Search;
import com.chriniko.searchadsservice.domain.SearchResult;
import com.chriniko.searchadsservice.dto.SearchAdResponse;
import com.chriniko.searchadsservice.error.InvalidAdIdException;
import org.junit.jupiter.api.Test;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AdSearchServiceTest {

    @Test
    void process_works_as_expected_visitor_scrolling_case() throws Exception {

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


        Field searchesBySearchIdField = ReflectionUtils.findField(AdSearchService.class, "searchesBySearchId");
        searchesBySearchIdField.setAccessible(true);
        Map<String, Search> searchesBySearchId = (Map<String, Search>) searchesBySearchIdField.get(service);

        assertNotNull(searchesBySearchId.get(searchAdResponse.getSearchId()));


        Field generatedAdIdsField = ReflectionUtils.findField(AdSearchService.class, "generatedAdIds");
        generatedAdIdsField.setAccessible(true);
        Set<String> generatedAdIds = (Set<String>) generatedAdIdsField.get(service);

        assertEquals(searchAdResponse.getNumberOfResults(), generatedAdIds.size());


        Field clientSearchStateBySessionIdField = ReflectionUtils.findField(AdSearchService.class, "clientSearchStateBySessionId");
        clientSearchStateBySessionIdField.setAccessible(true);
        Map<String, ClientSearchState> clientSearchStateBySessionId = (Map<String, ClientSearchState>) clientSearchStateBySessionIdField.get(service);

        assertNotNull(clientSearchStateBySessionId.get(sessionId));
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