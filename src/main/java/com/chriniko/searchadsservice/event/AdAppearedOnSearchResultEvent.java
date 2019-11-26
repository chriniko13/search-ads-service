package com.chriniko.searchadsservice.event;

public class AdAppearedOnSearchResultEvent extends AdEvent {

    public AdAppearedOnSearchResultEvent(String adId) {
        super(adId);
    }
}
