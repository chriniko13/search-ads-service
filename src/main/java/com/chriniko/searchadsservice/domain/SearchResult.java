package com.chriniko.searchadsservice.domain;

import com.chriniko.searchadsservice.dto.SearchAdResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchResult {

    private SearchAdResponse searchAdResponse;
    private AdEventsToTriggerHolder adEventsToTriggerHolder;

}
