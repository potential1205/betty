package org.example.betty.domain.exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.contract.LiquidityPool;
import org.example.betty.domain.exchange.entity.Token;
import org.example.betty.domain.exchange.entity.TokenPrice;
import org.example.betty.domain.exchange.repository.TokenPriceRepository;
import org.example.betty.domain.exchange.repository.TokenRepository;
import org.example.betty.common.util.Web3ContractUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.protocol.Web3j;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PriceServiceImpl implements PriceService {

    private final Web3j web3j;
    private final Web3ContractUtil contractUtil;
    private final TokenRepository tokenRepository;
    private final TokenPriceRepository tokenPriceRepository;

    @Value("${BET_ADDRESS}")
    private String betTokenAddress;

    @Value("${DSB_POOL}")
    private String dsbPoolAddress;
    @Value("${SSG_POOL}")
    private String ssgPoolAddress;
    @Value("${LGT_POOL}")
    private String lgtPoolAddress;
    @Value("${LTG_POOL}")
    private String ltgPoolAddress;
    @Value("${KWH_POOL}")
    private String kwhPoolAddress;
    @Value("${NCD_POOL}")
    private String ncdPoolAddress;
    @Value("${KTW_POOL}")
    private String ktwPoolAddress;
    @Value("${KIA_POOL}")
    private String kiaPoolAddress;
    @Value("${SSL_POOL}")
    private String sslPoolAddress;
    @Value("${HWE_POOL}")
    private String hwePoolAddress;

    @Transactional
    @Override
    public void syncAllPrices() {
        // 각 팬토큰의 유동성 풀 주소 기반으로 가격 갱신
        updatePrice("DSB", dsbPoolAddress);
        updatePrice("SSG", ssgPoolAddress);
        updatePrice("LGT", lgtPoolAddress);
        updatePrice("LTG", ltgPoolAddress);
        updatePrice("KWH", kwhPoolAddress);
        updatePrice("NCD", ncdPoolAddress);
        updatePrice("KTW", ktwPoolAddress);
        updatePrice("KIA", kiaPoolAddress);
        updatePrice("SSL", sslPoolAddress);
        // updatePrice("HWE", hwePoolAddress); // 필요시 주석 해제
    }

    @Transactional
    protected void updatePrice(String tokenName, String poolAddress) {
        try {
            // 유동성 풀 컨트랙트 로드
            LiquidityPool pool = LiquidityPool.load(
                    poolAddress,
                    web3j,
                    contractUtil.newReadOnlyTransactionManager(),
                    contractUtil.getContractGasProvider()
            );

            // 유동성 조회
            var reserves = pool.getReserves().send();
            BigInteger betReserve = reserves.component1();
            BigInteger fanTokenReserve = reserves.component2();

            if (fanTokenReserve.equals(BigInteger.ZERO)) {
                //log.warn("[{}] 팬토큰 수량 0 → 가격 동기화 생략", tokenName);
                return;
            }

            // 가격 계산 (BET / 팬토큰)
            BigDecimal bet = new BigDecimal(betReserve);
            BigDecimal fan = new BigDecimal(fanTokenReserve);
            BigDecimal price = bet.divide(fan, 18, RoundingMode.HALF_UP)
                    .setScale(8, RoundingMode.HALF_UP);

            // 토큰 정보 조회 (token_id 저장을 위해 필수)
            Token token = tokenRepository.findByTokenName(tokenName)
                    .orElseThrow(() -> new IllegalArgumentException("Token not found: " + tokenName));

            // 가격 저장
            TokenPrice tokenPrice = TokenPrice.builder()
                    .token(token) // JPA 연관관계 저장
                    .tokenName(tokenName)
                    .price(price)
                    .updatedAt(LocalDateTime.now())
                    .build();

            tokenPriceRepository.save(tokenPrice);

            //log.info("Price Sync completed: {} = {} BET", tokenName, price);

        } catch (Exception e) {
            //log.error("Price Sync failed: {}", tokenName, e);
        }
    }
}
