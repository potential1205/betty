package org.example.betty.common.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.auth.service.TokenService;
import org.example.betty.exception.BusinessException;
import org.example.betty.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class SessionUtil {

    private final TokenService tokenService;

    @Qualifier("redisTemplate")
    private final RedisTemplate<String, Object> redis1;

    public void setSession(String walletAddress, String accessToken, Duration ttl) {
        redis1.opsForValue().set(walletAddress, accessToken, ttl);
    }

    public String getSession(String accessToken) {
        log.info("accessToken 검증 전");
        if (accessToken == null || accessToken.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ACCESS_TOKEN);
        }
        log.info("accessToken 존재");
        String accessTokenBody = accessToken.substring(7).trim();

        String walletAddress = tokenService.getSubjectFromToken(accessTokenBody);

        log.info("accessToken에서 지갑 주소 추출:" + walletAddress);

        Object stored = redis1.opsForValue().get(walletAddress);

        log.info("redis 세션 조회:" + walletAddress);

        if (!Objects.equals(accessTokenBody, stored)) {
            throw new BusinessException(ErrorCode.INVALID_SESSION);
        }

        log.info("redis 세션 조회 성공:" + walletAddress);

        return walletAddress;
    }

    public String getWalletAddress(String accessToken) {
        if (accessToken == null || accessToken.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ACCESS_TOKEN);
        }

        String accessTokenBody = accessToken.substring(7).trim();

        return tokenService.getSubjectFromToken(accessTokenBody);
    }

    public void deleteSession(String walletAddress) {
        redis1.delete(walletAddress);
    }
}
