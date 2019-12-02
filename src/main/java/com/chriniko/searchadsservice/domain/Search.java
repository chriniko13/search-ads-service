package com.chriniko.searchadsservice.domain;

import com.chriniko.searchadsservice.error.SearchPreferenceException;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Getter
public class Search {

    protected final String searchId;
    protected final List<Ad> ads;
    protected final int numberOfResults;

    protected final String searchText;
    protected final Date createdAt;

    public Search(String searchText) {

        Random rnd = ThreadLocalRandom.current();

        this.searchText = searchText;

        this.numberOfResults = Math.max(50, rnd.nextInt(500));

        ads = new ArrayList<>(numberOfResults);
        for (int i = 0; i < numberOfResults; i++) {
            ads.add(new Ad(searchText));
        }

        searchId = UUID.randomUUID().toString();
        createdAt = Calendar.getInstance().getTime();
    }

    public int getTotalPages(int pageSize) {
        return (int) Math.ceil((double) numberOfResults / pageSize);
    }

    public Set<Ad> getAds(int offset, int size) {

        if (size <= 0) {
            throw new SearchPreferenceException("not valid size parameter received");
        }

        int totalPages = getTotalPages(size);

        // Note: offset ---> [0, totalPages-1]
        if (offset < 0 || offset > totalPages - 1) {
            throw new SearchPreferenceException("not valid offset (" + offset + ") parameter received, valid range for offset is: [0, " + (totalPages - 1) + "]");
        }

        int skipRecords = offset * size;

        final List<Ad> result;
        if (offset != totalPages - 1) {
            result = this.ads.subList(skipRecords, skipRecords + size);
        } else {
            result = this.ads.subList(skipRecords, ads.size());
        }

        return new LinkedHashSet<>(result);
    }

    public Collection<? extends String> allAdIds() {
        return ads.stream().map(Ad::getId).collect(Collectors.toList());
    }
}
