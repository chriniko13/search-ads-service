package com.chriniko.searchadsservice.client;

import com.chriniko.searchadsservice.domain.Ad;
import com.chriniko.searchadsservice.domain.PagedAds;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ClientAdsSimulator {

    private final String serviceUrl;

    private final PagedAds pagedAds;

    private int currentDisplayedPage;
    private List<Ad> currentDisplayedAds;

    private final RestTemplate restTemplate;

    public ClientAdsSimulator(String serviceUrl, PagedAds pagedAds) {
        this.serviceUrl = serviceUrl;
        this.pagedAds = pagedAds;
        this.restTemplate = new RestTemplate();

        currentDisplayedAds = this.pagedAds.getAds().get(0);
        currentDisplayedPage = 0;
    }

    public void clicked(String adId) {
        notify(Collections.singletonList(adId), AdEvent.CLICKED);
    }

    public void proceedToNextPage() {
        // Note: if we are on the last page, exit.
        if (currentDisplayedPage >= pagedAds.getAds().size() - 1) {
            return;
        }

        currentDisplayedPage++;
        currentDisplayedAds = this.pagedAds.getAds().get(currentDisplayedPage);

        notify(currentDisplayedAds.stream().map(Ad::getId).collect(Collectors.toList()), AdEvent.APPEARED);
    }

    public void proceedToPreviousPage() {
        // Note: if we are on the first page, exit.
        if (currentDisplayedPage == 0) {
            return;
        }

        currentDisplayedPage--;
        currentDisplayedAds = this.pagedAds.getAds().get(currentDisplayedPage);
        // Important Note: we have visited this page, so we do not fire again events - business constraint.
    }

    private void notify(List<String> adIds, AdEvent adEvent) {

        switch (adEvent) {
            case CLICKED:
                //TODO...
                break;

            case APPEARED:
                //TODO...
                break;
        }

    }

    enum AdEvent {
        CLICKED, APPEARED
    }

}
