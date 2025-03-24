package org.example.betty.domain.auth.service;

import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.example.betty.common.jwt.JwtProvider;
import org.example.betty.exception.BusinessException;
import org.example.betty.exception.ErrorCode;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, String> redis;

    public String login(String idToken)  {
        try {
            String address = extractAddress(idToken);
            String accessToken = jwtProvider.createAccessToken(address);
            redis.opsForValue().set("session:" + accessToken, "valid", Duration.ofHours(1));

            return accessToken;

        } catch (ParseException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private String extractAddress(String idToken) throws ParseException {
        SignedJWT jwt = SignedJWT.parse(idToken);

        List<Map<String, Object>> wallets = (List<Map<String, Object>>) jwt.getJWTClaimsSet().getClaim("wallets");

        return wallets.get(0).get("address").toString();
    }
}
