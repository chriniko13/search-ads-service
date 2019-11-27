package com.chriniko.searchadsservice.domain;

import com.chriniko.searchadsservice.doc.NotThreadSafe;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NotThreadSafe // Note: if we want to mutate the attributes, then it needs synchronization or something like LongAdder.

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdStatistics {

    private long includedInSearchCount = 0;
    private long appearedOnSearchCount = 0;
    private long clickedCount = 0;


    public void incrementClicks() {
        clickedCount++;
    }

    public void incrementAppearedOnSearches() {
        appearedOnSearchCount++;
    }

    public void incrementIncludedInSearches() {
        includedInSearchCount++;
    }
}
