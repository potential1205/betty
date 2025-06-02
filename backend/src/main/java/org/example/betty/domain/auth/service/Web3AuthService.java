package org.example.betty.domain.auth.service;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;

@Service
@Slf4j
public class Web3AuthService {

    @Value("${web3auth.jwks-url}")
    private String jwksUrl;

    private final JWKSource<SecurityContext> jwkSource;

    public Web3AuthService(@Value("${web3auth.jwks-url}") String jwksUrl) throws Exception {
        this.jwkSource = new RemoteJWKSet<>(new URL(jwksUrl));
    }

    public JWTClaimsSet verifyIdToken(String idToken) throws Exception {
        SignedJWT signedJWT = SignedJWT.parse(idToken);

        DefaultJWTProcessor<SecurityContext> processor = new DefaultJWTProcessor<>();
        JWSAlgorithm alg = signedJWT.getHeader().getAlgorithm();
        processor.setJWSKeySelector(new JWSVerificationKeySelector<>(alg, jwkSource));

        JWTClaimsSet claims = processor.process(signedJWT, null);

        if (claims.getExpirationTime().before(new Date())) {
            throw new RuntimeException("ID token has expired");
        }

        return claims;
    }
}
