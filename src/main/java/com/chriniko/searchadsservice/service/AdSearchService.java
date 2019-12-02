package com.chriniko.searchadsservice.service;

import com.chriniko.searchadsservice.domain.*;
import com.chriniko.searchadsservice.dto.SearchAdResponse;
import com.chriniko.searchadsservice.error.InvalidAdIdException;
import com.chriniko.searchadsservice.event.internal.UserSessionTtlAwareEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class AdSearchService implements UserSessionTtlAware {

    private final UserSessionService userSessionService;

    private final ConcurrentHashMap<String /*searchId*/, Search> searchesBySearchId;

    private final ConcurrentHashMap.KeySetView<String, Boolean> generatedAdIds = ConcurrentHashMap.newKeySet();

    private final ConcurrentHashMap<String /*sessionId*/, ClientSearchState> clientSearchStateBySessionId;

    @Autowired
    public AdSearchService(UserSessionService userSessionService) {
        searchesBySearchId = new ConcurrentHashMap<>();
        clientSearchStateBySessionId = new ConcurrentHashMap<>();
        this.userSessionService = userSessionService;
    }


    public void isValidAdId(String adId) {
        if (!generatedAdIds.contains(adId)) {
            throw new InvalidAdIdException("the provided ad id: " + adId + " is not valid");
        }
    }

    public void isValidAdId(List<String> adIds) {
        adIds.forEach(this::isValidAdId);
    }

    public SearchResult process(String sessionId,
                                String searchId, int offset, int size,
                                String input) {

        if (sessionId == null) {
            return initVisit(offset, size, input);
        } else {
            return revisit(sessionId, searchId, offset, size, input);
        }
    }

    private SearchResult initVisit(int offset, int size, String input) {

        final Search search = new Search(input);
        searchesBySearchId.put(search.getSearchId(), search);

        generatedAdIds.addAll(search.allAdIds());

        String generatedSessionId = userSessionService.generate();

        return getFirstSearchResult(offset, size, search, generatedSessionId);
    }

    private SearchResult revisit(String sessionId, String searchId, int offset, int size, String input) {

        if (searchId != null) { // Note: visitor has already performed a search (searchId), just scrolling through the results...

            return scrolling(sessionId, searchId, offset, size);

        } else { // Note: visitor, wants to perform a new search request...

            final Search search = new Search(input);
            searchesBySearchId.put(search.getSearchId(), search);

            generatedAdIds.addAll(search.allAdIds());

            return getFirstSearchResult(offset, size, search, sessionId);
        }
    }

    private SearchResult scrolling(String sessionId, String searchId, int offset, int size) {


        Search search = searchesBySearchId.get(searchId);

        Set<Ad> adsToDisplay = search.getAds(offset, size);

        ClientSearchState clientSearchState = clientSearchStateBySessionId.get(sessionId);

        Set<String> adIdsAppearedOnSearchUntilNow = clientSearchState.getAdIdsAppearedOnSearch();

        // Note: find which have not appeared and fire only these.
        final Set<String> adIdsToFireAppearedOnSearch = new LinkedHashSet<>();
        for (Ad ad : adsToDisplay) {
            String id = ad.getId();

            if (!adIdsAppearedOnSearchUntilNow.contains(id)) {
                adIdsToFireAppearedOnSearch.add(id);
            }
        }

        clientSearchState.addAdIdsAppearedOnSearch(adIdsToFireAppearedOnSearch);


        SearchAdResponse response = new SearchAdResponse(searchId,
                search.getNumberOfResults(),
                offset, size, search.getTotalPages(size),
                adsToDisplay
        );

        return new SearchResult(
                response,
                AdEventsToTriggerHolder.initForAdIdsAppearedOnSearch(adIdsToFireAppearedOnSearch),
                sessionId
        );
    }


    private SearchResult getFirstSearchResult(int offset, int size, Search search, String sessionId) {

        Set<Ad> ads = search.getAds(offset, size);
        List<Ad> allAds = search.getAds();

        Set<String> adIdsAppearedOnSearch = ads.stream().map(Ad::getId).collect(Collectors.toSet());
        Set<String> adIdsIncludedInSearch = allAds.stream().map(Ad::getId).collect(Collectors.toSet());


        ClientSearchState clientSearchState = new ClientSearchState(search.getSearchId());
        clientSearchState.addAdIdsAppearedOnSearch(adIdsAppearedOnSearch);
        clientSearchState.addAdIdsIncludedInSearch(adIdsIncludedInSearch);

        clientSearchStateBySessionId.put(sessionId, clientSearchState);

        SearchAdResponse searchAdResponse = new SearchAdResponse(search.getSearchId(),
                search.getNumberOfResults(),
                offset, size, search.getTotalPages(size),
                ads
        );

        return new SearchResult(
                searchAdResponse,
                AdEventsToTriggerHolder.init(adIdsAppearedOnSearch, adIdsIncludedInSearch),
                sessionId
        );
    }

    @Override
    public void onApplicationEvent(UserSessionTtlAwareEvent userSessionTtlAwareEvent) {

        String sessionIdExpired = userSessionTtlAwareEvent.getSessionId();
        clientSearchStateBySessionId.remove(sessionIdExpired);
    }
}
