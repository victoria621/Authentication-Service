package com.innowise.authenticationservice.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

class RefreshTokenTest {

    @Test
    void testGettersAndSetters() {
        User user = new User();
        user.setId(1L);

        RefreshToken token = new RefreshToken();
        token.setId(1L);
        token.setToken("test-token-123");
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.of(2025, 1, 8, 12, 0, 0));

        assertThat(token.getId()).isEqualTo(1L);
        assertThat(token.getToken()).isEqualTo("test-token-123");
        assertThat(token.getUser()).isEqualTo(user);
        assertThat(token.getExpiresAt()).isNotNull();
    }

    @Test
    void testConstructor() {
        User user = new User();
        user.setId(1L);

        LocalDateTime expiresAt = LocalDateTime.of(2025, 1, 7, 12, 0, 0);
        RefreshToken token = new RefreshToken(1L, user, "test-token", expiresAt);

        assertThat(token.getId()).isEqualTo(1L);
        assertThat(token.getUser()).isEqualTo(user);
        assertThat(token.getToken()).isEqualTo("test-token");
        assertThat(token.getExpiresAt()).isEqualTo(expiresAt);
    }
}