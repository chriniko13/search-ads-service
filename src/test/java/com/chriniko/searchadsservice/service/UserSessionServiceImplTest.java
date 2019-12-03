package com.chriniko.searchadsservice.service;

import com.chriniko.searchadsservice.domain.UserSession;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class UserSessionServiceImplTest {

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Test
    void generate_and_ttl_expire_works_as_expected() throws Exception {


        // given
        UserSessionService userSessionService = new UserSessionServiceImpl(applicationEventPublisher);

        Field userSessionTtlInSecsField = userSessionService.getClass().getDeclaredField("USER_SESSION_TTL_IN_SECS");
        setFinalStatic(userSessionTtlInSecsField, 5 /*seconds*/);

        Field userSessionsByIdField = userSessionService.getClass().getDeclaredField("userSessionsById");
        userSessionsByIdField.setAccessible(true);
        Map<String, UserSession> userSessionsById = (Map<String, UserSession>) userSessionsByIdField.get(userSessionService);

        // when
        String sessionId = userSessionService.generate();


        // then
        assertNotNull(sessionId);

        Awaitility.await().atMost(15, TimeUnit.SECONDS).untilAsserted(() -> {
            assertTrue(userSessionsById.isEmpty());
        });

    }

    static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);
    }
}