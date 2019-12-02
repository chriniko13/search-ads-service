package com.chriniko.searchadsservice.service;

import com.chriniko.searchadsservice.domain.UserSession;
import com.chriniko.searchadsservice.event.internal.UserSessionTtlAwareEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class UserSessionServiceImpl implements UserSessionService {

    private static final int USER_SESSION_TTL_IN_SECS = 3 * 60; // Note: 3 minutes.

    private final ConcurrentHashMap<String, UserSession> userSessionsById;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public UserSessionServiceImpl(ApplicationEventPublisher applicationEventPublisher) {
        userSessionsById = new ConcurrentHashMap<>();

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("UserSession-TtlHousekeeper");
                return t;
            }
        });

        scheduledExecutorService.scheduleWithFixedDelay(() -> {

            Instant now = Instant.now();

            Iterator<Map.Entry<String, UserSession>> iterator = userSessionsById.entrySet().iterator();

            while (iterator.hasNext()) {

                Map.Entry<String, UserSession> entry = iterator.next();

                UserSession userSession = entry.getValue();
                String userSessionId = userSession.getId();
                Instant created = userSession.getCreated();

                long millisSinceSessionCreation = Duration.between(created, now).toMillis();

                long secondsSinceSessionCreation = TimeUnit.SECONDS.convert(millisSinceSessionCreation, TimeUnit.MILLISECONDS);

                if (secondsSinceSessionCreation >= USER_SESSION_TTL_IN_SECS) {
                    iterator.remove();

                    // Note: notify other components to maintain state also.
                    applicationEventPublisher.publishEvent(new UserSessionTtlAwareEvent(this, userSessionId));
                }
            }

        }, 3, 3, TimeUnit.SECONDS);
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public String generate() {

        UserSession userSession = new UserSession();
        userSessionsById.put(userSession.getId(), userSession);

        return userSession.getId();
    }

}
