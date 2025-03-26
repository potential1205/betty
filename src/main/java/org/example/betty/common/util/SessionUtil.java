package org.example.betty.common.util;

import lombok.RequiredArgsConstructor;
import org.example.betty.domain.auth.service.TokenService;
import org.example.betty.exception.BusinessException;
import org.example.betty.exception.ErrorCode;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class SessionUtil {

    private final TokenService tokenService;
    private final RedisTemplate<String, String> redisSession;

    public void setSession(String walletAddress, String accessToken, Duration ttl) {
        redisSession.opsForValue().set(walletAddress, accessToken, ttl);
    }

    public String getSession(String accessToken) {
        if (accessToken == null || accessToken.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ACCESS_TOKEN);
        }

        String accessTokenBody = accessToken.substring(7).trim();

        String walletAddress = tokenService.getSubjectFromToken(accessTokenBody);

        Object stored = redisSession.opsForValue().get(walletAddress);

        if (!Objects.equals(accessTokenBody, stored)) {
            throw new BusinessException(ErrorCode.INVALID_SESSION);
        }

        return walletAddress;
    }

    public void deleteSession(String walletAddress) {
        redisSession.delete(walletAddress);
    }
}
