package com.chriniko.searchadsservice.it;

import com.chriniko.searchadsservice.domain.PagedAds;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

public abstract class Specification {

    protected String getBaseUrl(int port) {
        return "http://localhost:" + port + "/search-ads";
    }

    protected HttpEntity<String> createHttpEntity(String payload) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");

        return new HttpEntity<>(payload, httpHeaders);
    }

    protected HttpEntity<PagedAds> createHttpEntity(PagedAds pagedAds) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");

        return new HttpEntity<>(pagedAds, httpHeaders);
    }

}
