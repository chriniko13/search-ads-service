package com.chriniko.searchadsservice.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class AdEventsToTriggerHolder {

    private List<String> adIdsIncludedInSearch;
    private List<String> adIdsAppearedOnSearch;

    private AdEventsToTriggerHolder() {
    }

    public static AdEventsToTriggerHolder initForAdIdsAppearedOnSearch(Set<String> adIdsAppearedOnSearch) {
        AdEventsToTriggerHolder holder = new AdEventsToTriggerHolder();
        holder.setAdIdsIncludedInSearch(Collections.emptyList());
        holder.setAdIdsAppearedOnSearch(new ArrayList<>(adIdsAppearedOnSearch));
        return holder;
    }

    public static AdEventsToTriggerHolder init(List<String> adIds) {
        AdEventsToTriggerHolder holder = new AdEventsToTriggerHolder();
        holder.setAdIdsIncludedInSearch(adIds);
        holder.setAdIdsAppearedOnSearch(adIds);
        return holder;
    }
}
