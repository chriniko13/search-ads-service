package com.chriniko.searchadsservice.event;

public class AdIncludedInSearchProcessEvent extends AdEvent {

    public AdIncludedInSearchProcessEvent(String adId) {
        super(adId);
    }
}
