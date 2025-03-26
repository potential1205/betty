package org.example.betty.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class SessionUtil {

    private final RedisTemplate<String, String> redisSession;

    public void setSession(String walletAddress, String accessToken, Duration ttl) {
        redisSession.opsForValue().set(walletAddress, accessToken, ttl);
    }

    public boolean isSessionValid(String walletAddress, String accessToken) {
        return Objects.equals(
                accessToken,
                redisSession.opsForValue().get(walletAddress)
        );
    }

    public void deleteSession(String walletAddress) {
        redisSession.delete(walletAddress);
    }
}
