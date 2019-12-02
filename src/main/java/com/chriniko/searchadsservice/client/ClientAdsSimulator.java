package com.chriniko.searchadsservice.client;

import com.chriniko.searchadsservice.domain.Ad;
import com.chriniko.searchadsservice.dto.AdClickedRequest;
import com.chriniko.searchadsservice.dto.AdsAppearedOnSearchRequest;
import com.chriniko.searchadsservice.dto.SearchAdRequest;
import com.chriniko.searchadsservice.dto.SearchAdResponse;
import lombok.Getter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;


@Getter
public class ClientAdsSimulator {

    private final String serviceUrl;

    private int offset;
    private int size;
    private int totalPages;

    private String sessionId;
    private String searchId;

    private List<Ad> currentDisplayedAds;

    private final RestTemplate restTemplate;

    public ClientAdsSimulator(String serviceUrl, String searchText) {
        this.serviceUrl = serviceUrl;

        this.restTemplate = new RestTemplate();

        this.offset = 0;
        this.size = 10;

        this.currentDisplayedAds = initSearch(searchText);
    }

    public List<Ad> initSearch(String searchText) {
        SearchAdRequest searchAdRequest = new SearchAdRequest(searchText);

        ResponseEntity<SearchAdResponse> response = restTemplate.exchange(
                serviceUrl + "/search-ads",
                HttpMethod.POST,
                new HttpEntity<>(searchAdRequest),
                SearchAdResponse.class
        );

        return extractResult(response);
    }

    public List<Ad> search(String searchText) {
        SearchAdRequest searchAdRequest = new SearchAdRequest(searchText);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("sessionId", sessionId);

        String url = UriComponentsBuilder.fromHttpUrl(serviceUrl + "/search-ads")
                .toUriString();

        ResponseEntity<SearchAdResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(searchAdRequest, httpHeaders),
                SearchAdResponse.class
        );

        return extractResult(response);
    }

    private List<Ad> search(String searchId, String sessionId, int offset, int size) {
        SearchAdRequest searchAdRequest = new SearchAdRequest("");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("sessionId", sessionId);

        String url = UriComponentsBuilder.fromHttpUrl(serviceUrl + "/search-ads")
                .queryParam("searchId", searchId)
                .queryParam("offset", offset)
                .queryParam("size", size)
                .toUriString();

        ResponseEntity<SearchAdResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(searchAdRequest, httpHeaders),
                SearchAdResponse.class
        );

        return extractResult(response);
    }

    public List<Ad> scrollThroughSearch(int offsetToUse) {
        return search(searchId, sessionId, offsetToUse, size);
    }

    // Note: this method is used to simulate the change of page size (and possible offset) from the user/client.
    public List<Ad> scrollThroughSearch(int offsetToUse, int sizeToUse) {
        return search(searchId, sessionId, offsetToUse, sizeToUse);
    }


    public void clicked(String adId, boolean fromCampaign) {
        notify(
                Collections.singletonList(adId),
                fromCampaign ? AdEvent.CLICKED_FROM_CAMPAIGN : AdEvent.CLICKED_FROM_SEARCH
        );
    }

    public void proceedToNextPage() {
        // Note: if we are on the last page, exit.
        if (offset >= totalPages - 1) {
            return;
        }

        currentDisplayedAds = search(searchId, sessionId, offset + 1, size);
    }

    private List<Ad> extractResult(ResponseEntity<SearchAdResponse> response) {
        SearchAdResponse searchAdResponse = response.getBody();

        this.offset = searchAdResponse.getCurrentPage();
        this.size = searchAdResponse.getPageSize();
        this.totalPages = searchAdResponse.getTotalPages();
        this.sessionId = response.getHeaders().getFirst("sessionId");
        this.searchId = searchAdResponse.getSearchId();

        return new ArrayList<>(searchAdResponse.getAds());
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
