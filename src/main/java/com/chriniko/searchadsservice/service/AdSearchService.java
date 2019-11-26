package com.chriniko.searchadsservice.service;

import com.chriniko.searchadsservice.domain.Search;
import com.chriniko.searchadsservice.error.InvalidAdIdException;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class AdSearchService {

    private static final int PAGE_SIZE = 10;

    // Note: this maps are used more for auditing - proof of concept - testing.
    @Getter
    private final ConcurrentHashMap<String /*searchId*/, Search> searchesBySearchId;

    @Getter
    private final ConcurrentHashMap<String /*searchText*/, ConcurrentLinkedQueue<Search>> searchesBySearchText;

    @Getter
    private final ConcurrentHashMap.KeySetView<String, Boolean> generatedAdIds = ConcurrentHashMap.newKeySet();

    public AdSearchService() {
        searchesBySearchId = new ConcurrentHashMap<>();
        searchesBySearchText = new ConcurrentHashMap<>();
    }

    public Search process(String input) {

        final Search search = new Search(input, PAGE_SIZE);

        searchesBySearchId.put(search.getSearchId(), search);

        searchesBySearchText.compute(search.getSearchText(), (searchText, searches) -> {

            if (searches == null) {
                searches = new ConcurrentLinkedQueue<>();
            }
            searches.add(search);

            return searches;
        });

        generatedAdIds.addAll(
                search.getPagedAds().allAdIds()
        );

        return search;
    }

    public void isValidAdId(String adId) {
        if (!generatedAdIds.contains(adId)) {
            throw new InvalidAdIdException("the provided ad id: " + adId + " is not valid");
        }
    }

    public void isValidAdId(List<String> adIds) {
        adIds.forEach(this::isValidAdId);
    }
}
