package com.chriniko.searchadsservice.resource;

import com.chriniko.searchadsservice.domain.AdStatistics;
import com.chriniko.searchadsservice.service.AdSearchService;
import com.chriniko.searchadsservice.service.AdStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ad-statistics")
public class AdStatisticsResource {

    private final AdStatisticsService adStatisticsService;
    private final AdSearchService adSearchService;

    @Autowired
    public AdStatisticsResource(AdStatisticsService adStatisticsService, AdSearchService adSearchService) {
        this.adStatisticsService = adStatisticsService;
        this.adSearchService = adSearchService;
    }

    @GetMapping(path = "/{adId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody HttpEntity<AdStatistics> get(@PathVariable("adId") String adId) {
        adSearchService.isValidAdId(adId);
        AdStatistics result = adStatisticsService.fetch(adId);
        return ResponseEntity.ok(result);
    }

}
