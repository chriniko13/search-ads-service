package com.chriniko.searchadsservice.domain;


import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserSession {

    private final String id = UUID.randomUUID().toString();
    private final Instant created = Instant.now();

}
