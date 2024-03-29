package com.chriniko.searchadsservice.service;

import com.chriniko.searchadsservice.domain.AdClickSource;
import com.chriniko.searchadsservice.domain.AdStatistics;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Service
public class AdStatisticsService {

    private final ConcurrentHashMap<String /*Note: adId*/, AdStatistics> adStatisticsByAdId;

    public AdStatisticsService() {
        adStatisticsByAdId = new ConcurrentHashMap<>();
    }

    public AdStatistics fetch(String adId) {
        AdStatistics adStatistics = adStatisticsByAdId.get(adId);
        if (adStatistics == null) {
            return new AdStatistics(); // Note: not calculated yet from kafka consumer (eventual consistent).
        }
        return adStatistics;
    }

    public void clicked(String adId, AdClickSource source) {
        adStatisticsByAdId.compute(adId, (_adId, adStatistics) -> {
            adStatistics = ensureStatisticsExist(adStatistics);

            if (source == AdClickSource.SEARCH) {
                adStatistics.incrementClicksFromSearch();
            } else {
                adStatistics.incrementClicksFromCampaign();
            }

            return adStatistics;
        });
    }

    public void includedInSearch(String adId) {
        updateStatistics(adId, AdStatistics::incrementIncludedInSearches);
    }

    public void appearedOnSearch(String adId) {
        updateStatistics(adId, AdStatistics::incrementAppearedOnSearches);
    }

    private void updateStatistics(String adId, Consumer<AdStatistics> adStatisticsConsumer) {
        adStatisticsByAdId.compute(adId, (_adId, adStatistics) -> {
            adStatistics = ensureStatisticsExist(adStatistics);
            adStatisticsConsumer.accept(adStatistics);
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
