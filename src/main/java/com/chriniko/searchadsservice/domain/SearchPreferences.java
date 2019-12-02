package com.chriniko.searchadsservice.domain;

import lombok.Data;

@Data
public class SearchPreferences {

    private final String searchId;

    private int offset;
    private int size;

    public SearchPreferences(String searchId, int offset, int size) {
        this.searchId = searchId;
        this.offset = offset;
        this.size = size;
    }

    public boolean same(int size) {
        return this.size == size;
    }

    public void update(int offset, int size) {
        this.offset = offset;
        this.size = size;
    }
}
