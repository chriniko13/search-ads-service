package com.chriniko.searchadsservice.service;

import com.chriniko.searchadsservice.event.internal.UserSessionTtlAwareEvent;
import org.springframework.context.ApplicationListener;

/*
    Note: this interface is implemented from concrete classes which store information
          based on user's sessionId ( eg: Map[SessionId, SearchSettings] ), so due to the fact that sessionId is expirable (ttl == time to live)
          we should clean - keep consistent these additional information.
 */
public interface UserSessionTtlAware extends ApplicationListener<UserSessionTtlAwareEvent> {
}
