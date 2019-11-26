package com.chriniko.searchadsservice.domain;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PagedAds {

    private final int totalPages;
    private final int pageSize;
    private final List<List<Ad>> ads;

    public PagedAds(int totalPages, int pageSize) {
        this.totalPages = totalPages;
        this.pageSize = pageSize;
        this.ads = new ArrayList<>(totalPages);
    }

    public List<String> allAdIds() {
        return ads.stream().flatMap(Collection::stream).map(Ad::getId).collect(Collectors.toList());
    }
}
