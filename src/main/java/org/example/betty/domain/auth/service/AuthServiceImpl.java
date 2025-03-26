package org.example.betty.domain.auth.service;

import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.betty.common.util.SessionUtil;
import org.example.betty.common.util.Web3AuthUtil;
import org.example.betty.exception.BusinessException;
import org.example.betty.exception.ErrorCode;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final SessionUtil sessionUtil;
    private final TokenService tokenService;
    private final Web3AuthService web3AuthService;

    @Override
    public String login(String idToken) {
        try {
            JWTClaimsSet claims = web3AuthService.verifyIdToken(idToken);

            @SuppressWarnings("unchecked")
            String walletAddress = Web3AuthUtil.extractWalletAddress(
                    (List<Map<String, Object>>)  claims.getClaim("wallets"));

            Date exp = claims.getExpirationTime();
            Duration ttl = Duration.between(Instant.now(), exp.toInstant());

            if (ttl.isNegative() || ttl.isZero()) {
                throw new BusinessException(ErrorCode.INVALID_ID_TOKEN);
            }

            String accessToken = tokenService.generateAccessToken(walletAddress, exp);

            sessionUtil.setSession(walletAddress, accessToken, ttl);

            return accessToken;
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.INVALID_ID_TOKEN);
        }
    }

    @Override
    public void logout(String accessToken) {
        if (accessToken.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ACCESS_TOKEN);
        }

        String accessTokenBody = accessToken.substring(7).trim();

        String wallet = tokenService.getSubjectFromToken(accessTokenBody);

        if (sessionUtil.isSessionValid(wallet, accessTokenBody)) {
            sessionUtil.deleteSession(wallet);
        } else {
            throw new BusinessException(ErrorCode.INVALID_SESSION);
        }
    }
}
