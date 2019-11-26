package com.chriniko.searchadsservice.service;

import com.chriniko.searchadsservice.domain.Search;
import com.chriniko.searchadsservice.error.InvalidAdIdException;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.jupiter.api.Assertions.*;

class AdSearchServiceTest {

    @Test
    void process() {

        // given
        String searchText = "ducati panigale";
        AdSearchService service = new AdSearchService();


        // when
        Search result = service.process(searchText);


        // then
        assertNotNull(result);
        assertTrue(result.getNumberOfResults() > 0);


        assertEquals(result, service.getSearchesBySearchId().get(result.getSearchId()));

        ConcurrentLinkedQueue<Search> searches = service.getSearchesBySearchText().get(searchText);
        assertEquals(1, searches.size());
        assertEquals(result, searches.peek());


        ConcurrentHashMap.KeySetView<String, Boolean> generatedAdIds = service.getGeneratedAdIds();
        assertEquals(result.getNumberOfResults(), generatedAdIds.size());
    }

    @Test
    void isValidAdId() {

        // given
        AdSearchService service = new AdSearchService();


        // when
        try {
            service.isValidAdId("foobar");
            fail();
        } catch (InvalidAdIdException error) { // then
            assertTrue(error.getMessage().contains("foobar"));
        }

    }
}