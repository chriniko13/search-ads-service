package com.chriniko.searchadsservice.client;

import com.chriniko.searchadsservice.domain.Ad;
import com.chriniko.searchadsservice.domain.PagedAds;
import com.chriniko.searchadsservice.dto.AdClickedRequest;
import com.chriniko.searchadsservice.dto.AdsAppearedOnSearchRequest;
import com.chriniko.searchadsservice.dto.SearchAdRequest;
import lombok.Getter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class ClientAdsSimulator {

    private final String serviceUrl;

    @Getter
    private final PagedAds pagedAds;

    private int currentDisplayedPage;

    @Getter
    private List<Ad> currentDisplayedAds;

    private final RestTemplate restTemplate;

    public ClientAdsSimulator(String serviceUrl, String searchText) {
        this.serviceUrl = serviceUrl;

        this.restTemplate = new RestTemplate();

        this.pagedAds = search(searchText);

        currentDisplayedAds = this.pagedAds.getAds().get(0);
        currentDisplayedPage = 0;

        notify(currentDisplayedAds.stream().map(Ad::getId).collect(Collectors.toList()), AdEvent.APPEARED);
    }

    public PagedAds search(String searchText) {
        SearchAdRequest searchAdRequest = new SearchAdRequest(searchText);
        ResponseEntity<PagedAds> response = restTemplate.exchange(
                serviceUrl + "/search-ads",
                HttpMethod.POST,
                new HttpEntity<>(searchAdRequest),
                PagedAds.class
        );
        return response.getBody();
    }

//    public void search(String searchId) {
//        //TODO...
//    }

    public void clicked(String adId, boolean fromCampaign) {
        notify(
                Collections.singletonList(adId),
                fromCampaign ? AdEvent.CLICKED_FROM_CAMPAIGN : AdEvent.CLICKED_FROM_SEARCH
        );
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
            case CLICKED_FROM_CAMPAIGN: {
                List<AdClickedRequest> adClickedRequests = adIds.stream().map(AdClickedRequest::new).collect(Collectors.toList());
                for (AdClickedRequest adClickedRequest : adClickedRequests) {
                    restTemplate.exchange(
                            serviceUrl + "/search-ads/clicked?campaignUrl=" + "someCampaign" + ThreadLocalRandom.current().nextInt(),
                            HttpMethod.POST,
                            new HttpEntity<>(adClickedRequest),
                            Void.class
                    );
                }
            }
            break;


            case CLICKED_FROM_SEARCH: {
                List<AdClickedRequest> adClickedRequests = adIds.stream().map(AdClickedRequest::new).collect(Collectors.toList());
                for (AdClickedRequest adClickedRequest : adClickedRequests) {
                    restTemplate.exchange(
                            serviceUrl + "/search-ads/clicked",
                            HttpMethod.POST,
                            new HttpEntity<>(adClickedRequest),
                            Void.class
                    );
                }
            }
            break;

            case APPEARED: {
                AdsAppearedOnSearchRequest adsAppearedOnSearchRequest = new AdsAppearedOnSearchRequest(adIds);
                restTemplate.exchange(
                        serviceUrl + "/search-ads/appeared",
                        HttpMethod.POST,
                        new HttpEntity<>(adsAppearedOnSearchRequest),
                        Void.class
                );
            }
            break;
        }

    }

    // ---

    enum AdEvent {
        CLICKED_FROM_SEARCH, CLICKED_FROM_CAMPAIGN, APPEARED
    }

}
