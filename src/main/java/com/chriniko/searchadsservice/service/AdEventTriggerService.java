package com.chriniko.searchadsservice.service;

import com.chriniko.searchadsservice.domain.AdClickSource;
import com.chriniko.searchadsservice.event.AdAppearedOnSearchResultEvent;
import com.chriniko.searchadsservice.event.AdClickedEvent;
import com.chriniko.searchadsservice.event.AdIncludedInSearchProcessEvent;
import com.chriniko.searchadsservice.producer.AdEventProducer;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
public class AdEventTriggerService {

    private final AdEventProducer adEventProducer;

    @Autowired
    public AdEventTriggerService(AdEventProducer adEventProducer) {
        this.adEventProducer = adEventProducer;
    }

    public void adClicked(String adId, String campaignUrl) {
        AdClickedEvent event = campaignUrl == null
                ? new AdClickedEvent(adId)
                : new AdClickedEvent(adId, AdClickSource.MAIL_CAMPAIGN);
        adEventProducer.send(event);
    }

    public void adsAppeared(List<String> adIds) {

        List<AdAppearedOnSearchResultEvent> events = adIds.stream()
                .map(AdAppearedOnSearchResultEvent::new)
                .collect(Collectors.toList());

        adEventProducer.send(events);
    }

    public void adsIncluded(List<String> adIds) {

        List<AdIncludedInSearchProcessEvent> events = adIds.stream()
                .map(AdIncludedInSearchProcessEvent::new)
                .collect(Collectors.toList());

        adEventProducer.send(events);
    }
}
