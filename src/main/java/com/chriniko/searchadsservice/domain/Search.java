package com.chriniko.searchadsservice.domain;

import lombok.Getter;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class Search {

    protected final String searchId;
    protected final PagedAds pagedAds;
    protected final int numberOfResults;

    protected final String searchText;
    protected final Date createdAt;

    public Search(String searchText, int pageSize) {

        Random rnd = ThreadLocalRandom.current();

        this.searchText = searchText;

        this.numberOfResults = Math.max(50, rnd.nextInt(500));

        int totalPages = (int) Math.ceil((double) numberOfResults / pageSize);

        int[] adsPerPage = getAdsPerPage(pageSize, totalPages);

        pagedAds = new PagedAds(totalPages, pageSize);

        populateAds(searchText, pageSize, totalPages, adsPerPage);

        searchId = UUID.randomUUID().toString();
        createdAt = Calendar.getInstance().getTime();
    }

    private int[] getAdsPerPage(int pageSize, int totalPages) {

        int[] adsPerPage = new int[totalPages];

        if (numberOfResults % pageSize == 0) {
            for (int i = 0; i < adsPerPage.length; i++) {
                adsPerPage[i] = pageSize;
            }
        } else {
            int remainder = numberOfResults % pageSize;
            for (int i = 0; i < adsPerPage.length - 1; i++) {
                adsPerPage[i] = pageSize;
            }
            adsPerPage[adsPerPage.length - 1] = remainder;
        }
        return adsPerPage;
    }

    private void populateAds(String searchText, int pageSize, int totalPages, int[] adsPerPage) {
        for (int i = 0; i < totalPages; i++) {

            List<Ad> ads = new ArrayList<>(adsPerPage[i]);

            for (int k = 0; k < adsPerPage[i]; k++) {
                ads.add(new Ad(searchText));
            }

            pagedAds.getAds().add(ads);
        }
    }

}
