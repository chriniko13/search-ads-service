package com.chriniko.searchadsservice.dto;

import com.chriniko.searchadsservice.domain.Ad;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchAdResponse {

    protected String searchId;
    protected int numberOfResults;

    private int currentPage;
    private int pageSize;
    private int totalPages;

    private Set<Ad> ads;

}
