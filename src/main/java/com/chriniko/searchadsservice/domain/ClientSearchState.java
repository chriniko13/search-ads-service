package com.chriniko.searchadsservice.domain;

import com.chriniko.searchadsservice.doc.NotThreadSafe;
import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
@NotThreadSafe
public class ClientSearchState {

    private final String searchId;

    private final Set<String> adIdsIncludedInSearch;
    private final Set<String> adIdsAppearedOnSearch;

    public ClientSearchState(String searchId) {
        this.searchId = searchId;
        this.adIdsIncludedInSearch = new LinkedHashSet<>();
        this.adIdsAppearedOnSearch = new LinkedHashSet<>();
    }


    public void addAdIdsIncludedInSearch(Set<String> ids) {
        adIdsIncludedInSearch.addAll(ids);
    }

    public void addAdIdsAppearedOnSearch(Set<String> ids) {
        adIdsAppearedOnSearch.addAll(ids);
    }


}
