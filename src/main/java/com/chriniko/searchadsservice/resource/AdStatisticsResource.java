package com.chriniko.searchadsservice.resource;

import com.chriniko.searchadsservice.domain.AdStatistics;
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

    @Autowired
    public AdStatisticsResource(AdStatisticsService adStatisticsService) {
        this.adStatisticsService = adStatisticsService;
    }

    @GetMapping(path = "/{adId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody HttpEntity<AdStatistics> get(@PathVariable("adId") String adId) {
        AdStatistics result = adStatisticsService.fetch(adId);
        return ResponseEntity.ok(result);
    }

}
