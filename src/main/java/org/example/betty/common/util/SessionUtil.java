package org.example.betty.common.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.auth.service.JWTService;
import org.example.betty.exception.BusinessException;
import org.example.betty.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class SessionUtil {

    private final JWTService jwtService;

    @Qualifier("redisTemplate")
    private final RedisTemplate<String, Object> redisTemplate;

    public void setSession(String walletAddress, Duration ttl) {
        redisTemplate.opsForValue().set(walletAddress, Boolean.TRUE, ttl);
    }

    public String getSession(String accessToken) {
        if (accessToken == null || accessToken.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ACCESS_TOKEN);
        }

        String accessTokenBody = accessToken.substring(7).trim();
        String walletAddress = jwtService.getSubjectFromToken(accessTokenBody);

        log.info("입력된 accessToken: " + accessTokenBody);
        log.info("accessToken에서 지갑 주소 추출:" + walletAddress);

        if (!Boolean.TRUE.equals(redisTemplate.hasKey(walletAddress))) {
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

        return jwtService.getSubjectFromToken(accessTokenBody);
    }

    public void deleteSession(String walletAddress) {
        redisTemplate.delete(walletAddress);
    }
}
