package com.chriniko.searchadsservice.service;

import com.chriniko.searchadsservice.domain.UserSession;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class UserSessionServiceImpl implements UserSessionService {

    private static final int USER_SESSION_TTL_IN_MINUTES = 3;

    private final ConcurrentHashMap<String, UserSession> userSessionsById;

    public UserSessionServiceImpl() {
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
                Instant created = userSession.getCreated();

                long minutesSinceSessionCreation = Duration.between(created, now).toMinutes();

                if (minutesSinceSessionCreation >= USER_SESSION_TTL_IN_MINUTES) {
                    iterator.remove();
                }
            }

        }, 3, 1, TimeUnit.SECONDS);
    }

    @Override
    public String generate() {

        UserSession userSession = new UserSession();
        userSessionsById.put(userSession.getId(), userSession);

        return userSession.getId();
    }

}
