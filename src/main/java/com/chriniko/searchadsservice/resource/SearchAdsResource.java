package com.chriniko.searchadsservice.resource;

import com.chriniko.searchadsservice.domain.AdEventsToTriggerHolder;
import com.chriniko.searchadsservice.domain.SearchResult;
import com.chriniko.searchadsservice.dto.AdClickedRequest;
import com.chriniko.searchadsservice.dto.AdsAppearedOnSearchRequest;
import com.chriniko.searchadsservice.dto.SearchAdRequest;
import com.chriniko.searchadsservice.dto.SearchAdResponse;
import com.chriniko.searchadsservice.service.AdEventTriggerService;
import com.chriniko.searchadsservice.service.AdSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/search-ads")
public class SearchAdsResource {

    private static final int PAGE_SIZE = 10;

    private final AdSearchService adSearchService;
    private final AdEventTriggerService adEventTriggerService;

    @Autowired
    public SearchAdsResource(AdSearchService adSearchService, AdEventTriggerService adEventTriggerService) {
        this.adSearchService = adSearchService;
        this.adEventTriggerService = adEventTriggerService;
    }

    // Note: this operation implicitly fires: {AdIncludedInSearchProcessEvent.java} == included event.
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody
    HttpEntity<SearchAdResponse> search(@RequestHeader(name = "sessionId", required = false) String sessionId,

                                        @RequestParam(name = "searchId", required = false) String searchId,
                                        @RequestParam(name = "offset", required = true, defaultValue = "0") int offset,
                                        @RequestParam(name = "size", required = true, defaultValue = "" + PAGE_SIZE) int size,

                                        @RequestBody @Valid SearchAdRequest req) {

        @NotNull String text = req.getText();

        SearchResult searchResult = adSearchService.process(sessionId,
                searchId, offset, size,
                text
        );

        fireEvents(searchResult);

        return ResponseEntity
                .ok()
                .header("sessionId", searchResult.getSessionId())
                .body(searchResult.getSearchAdResponse());
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @RequestMapping("/clicked")
    public @ResponseBody
    HttpEntity<Void> fireAdClickedEvent(@RequestBody @Valid AdClickedRequest req,
                                        @RequestParam(name = "campaignUrl", required = false) String campaignUrl) {

        @NotEmpty String adId = req.getAdId();
        adSearchService.isValidAdId(adId);

        adEventTriggerService.adClicked(adId, campaignUrl);

        return ResponseEntity.ok().build();
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @RequestMapping("/appeared")
    public @ResponseBody
    HttpEntity<Void> fireAdsAppearedOnSearchEvent(@RequestBody @Valid AdsAppearedOnSearchRequest req) {

        @NotEmpty List<@NotEmpty String> adIds = req.getAdIds();
        adSearchService.isValidAdId(adIds);

        adEventTriggerService.adsAppeared(adIds);

        return ResponseEntity.ok().build();
    }

    // ---

    private void fireEvents(SearchResult searchResult) {
        AdEventsToTriggerHolder adEventsToTriggerHolder = searchResult.getAdEventsToTriggerHolder();
        adEventTriggerService.adsAppeared(
                adEventsToTriggerHolder.getAdIdsAppearedOnSearch()
        );

        adEventTriggerService.adsIncluded(
                adEventsToTriggerHolder.getAdIdsIncludedInSearch()
        );
    }

}
