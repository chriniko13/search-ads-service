package com.chriniko.searchadsservice.domain;

import com.chriniko.searchadsservice.error.SearchPreferenceException;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SearchTest {

    @Test
    void search_works_as_expected() {

        // given
        int pageSize = 10;
        String searchText = "test-search-text";

        for (int run = 0; run < 5; run++) {
            System.out.println();

            // when
            Search search = new Search(searchText);
            int numberOfResults = search.getNumberOfResults();

            // then
            List<Ad> ads = search.getAds();
            assertEquals(numberOfResults, ads.size());

            assertNotNull(search.getCreatedAt());
            assertNotNull(search.getSearchId());
            assertNotNull(search.getSearchText());


            Set<Ad> diplayedAds = search.getAds(0, pageSize);
            assertEquals(pageSize, diplayedAds.size());


            int totalPages = search.getTotalPages(pageSize);
            List<Ad> accumulator = new LinkedList<>();
            for (int offset = 0; offset < totalPages; offset++) {
                accumulator.addAll(search.getAds(offset, pageSize));
            }
            assertEquals(numberOfResults, accumulator.size());

        }

    }

    @Test
    void search_works_as_expected_invalid_size_case() {


        // given
        Search search = new Search("foo");

        try {
            // when
            search.getAds(0, -100);
            fail();
        } catch (SearchPreferenceException e) { // then
            assertNotNull(e);
            assertEquals("not valid size parameter received", e.getMessage());
        }

    }

    @Test
    void search_works_as_expected_invalid_offset_case() {

        // given
        Search search = new Search("foo");
        int pageSize = 10;
        int totalPages = search.getTotalPages(pageSize);


        try {
            // when
            search.getAds(-100, pageSize);
            fail();
        } catch (SearchPreferenceException e) { // then
            assertNotNull(e);
            assertEquals("not valid offset (-100) parameter received, valid range for offset is: [0, " + (totalPages - 1) + "]", e.getMessage());
        }


        try {
            // when
            search.getAds(totalPages, pageSize);
            fail();
        } catch (SearchPreferenceException e) { // then
            assertNotNull(e);
            assertEquals("not valid offset (" + totalPages + ") parameter received, valid range for offset is: [0, " + (totalPages - 1) + "]", e.getMessage());
        }
    }
}