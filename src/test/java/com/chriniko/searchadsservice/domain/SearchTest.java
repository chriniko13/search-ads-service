package com.chriniko.searchadsservice.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SearchTest {

    @Test
    void search_works_as_expected() {

        // given
        int pageSize = 10;
        String searchText = "test-search-text";

        for (int run = 0; run < 5; run++) {

            // when
            Search search = new Search(searchText, pageSize);
            int numberOfResults = search.getNumberOfResults();

            // then
            PagedAds pagedAds = search.getPagedAds();

            int totalAds = 0;
            for (List<Ad> ads : pagedAds.getAds()) {
                totalAds += ads.size();
            }
            System.out.printf("totalAds = %d, numberOfResults = %d, pageSize = %d, totalPages = %d\n",
                    totalAds, numberOfResults, search.getPagedAds().getPageSize(), search.getPagedAds().getTotalPages());

            assertEquals(totalAds, numberOfResults);

            assertEquals(search.getPagedAds().getAds().size(), search.getPagedAds().getTotalPages());

            assertEquals(searchText, search.getSearchText());
            assertEquals(pageSize, search.getPagedAds().getPageSize());

            assertEquals(Math.ceil( (double)numberOfResults / pageSize), search.getPagedAds().getTotalPages());

            assertNotNull(search.getCreatedAt());
            assertNotNull(search.getSearchId());

        }

    }
}