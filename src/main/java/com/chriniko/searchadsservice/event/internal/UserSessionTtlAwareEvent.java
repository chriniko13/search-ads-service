package com.chriniko.searchadsservice.event.internal;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class UserSessionTtlAwareEvent extends ApplicationEvent {

    @Getter
    private final String sessionId;

    public UserSessionTtlAwareEvent(Object source, String sessionId) {
        super(source);
        this.sessionId = sessionId;
    }
}
