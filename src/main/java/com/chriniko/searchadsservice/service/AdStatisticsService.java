package com.chriniko.searchadsservice.service;

import com.chriniko.searchadsservice.domain.AdStatistics;
import com.chriniko.searchadsservice.error.InvalidAdIdException;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class AdStatisticsService {

    private final ConcurrentHashMap<String /*Note: adId*/, AdStatistics> adStatisticsByAdId;

    public AdStatisticsService() {
        adStatisticsByAdId = new ConcurrentHashMap<>();
    }

    public AdStatistics fetch(String adId) {
        AdStatistics adStatistics = adStatisticsByAdId.get(adId);
        if (adStatistics == null) {
            throw new InvalidAdIdException("no statistics exist for adId: " + adId);
        }
        return adStatistics;
    }

    public void clicked(String adId) {
        adStatisticsByAdId.compute(adId, (_adId, adStatistics) -> {
            adStatistics = ensureStatisticsExist(adStatistics);
            adStatistics.incrementClicks();

            return adStatistics;
        });
    }

    public void includedInSearch(String adId) {
        adStatisticsByAdId.compute(adId, (_adId, adStatistics) -> {
            adStatistics = ensureStatisticsExist(adStatistics);
            adStatistics.incrementIncludedInSearches();

            return adStatistics;
        });
    }

    public void appearedOnSearch(String adId) {
        adStatisticsByAdId.compute(adId, (_adId, adStatistics) -> {

            adStatistics = ensureStatisticsExist(adStatistics);
            adStatistics.incrementAppearedOnSearches();

            return adStatistics;
        });
    }

    private AdStatistics ensureStatisticsExist(AdStatistics adStatistics) {
        if (adStatistics == null) {
            adStatistics = new AdStatistics();
        }
        return adStatistics;
    }
}
