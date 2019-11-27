package com.chriniko.searchadsservice.resource;

import com.chriniko.searchadsservice.domain.PagedAds;
import com.chriniko.searchadsservice.domain.Search;
import com.chriniko.searchadsservice.dto.AdClickedRequest;
import com.chriniko.searchadsservice.dto.AdsAppearedOnSearchRequest;
import com.chriniko.searchadsservice.dto.SearchAdRequest;
import com.chriniko.searchadsservice.service.AdEventTriggerService;
import com.chriniko.searchadsservice.service.AdSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@RestController
@RequestMapping("/search-ads")
public class SearchAdsResource {

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
    public @ResponseBody HttpEntity<PagedAds> search(@RequestBody @Valid SearchAdRequest req) {

        @NotEmpty String text = req.getText();
        Search search = adSearchService.process(text);

        PagedAds pagedAds = search.getPagedAds();
        List<String> allAdIds = pagedAds.allAdIds();
        adEventTriggerService.adsIncluded(allAdIds);

        return ResponseEntity.ok(pagedAds);
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @RequestMapping("/clicked")
    public @ResponseBody HttpEntity<Void> fireAdClickedEvent(@RequestBody @Valid AdClickedRequest req,
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
    public @ResponseBody HttpEntity<Void> fireAdsAppearedOnSearchEvent(@RequestBody @Valid AdsAppearedOnSearchRequest req) {

        @NotEmpty List<@NotEmpty String> adIds = req.getAdIds();
        adSearchService.isValidAdId(adIds);

        adEventTriggerService.adsAppeared(adIds);

        return ResponseEntity.ok().build();
    }


}
