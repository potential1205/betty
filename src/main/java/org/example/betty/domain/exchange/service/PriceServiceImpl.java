package org.example.betty.domain.exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.contract.LiquidityPool;
import org.example.betty.domain.exchange.entity.TokenPrice;
import org.example.betty.domain.exchange.repository.TokenPriceRepository;
import org.example.betty.common.util.Web3ContractUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.protocol.Web3j;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PriceServiceImpl implements PriceService {

    private final Web3j web3j;
    private final Web3ContractUtil contractUtil;
    private final TokenPriceRepository tokenPriceRepository;

    @Value("${BTC_ADDRESS}")
    private String btcTokenAddress;

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
        updatePrice("DSB", dsbPoolAddress);
        updatePrice("SSG", ssgPoolAddress);
        updatePrice("LGT", lgtPoolAddress);
        updatePrice("LTG", ltgPoolAddress);
        updatePrice("KWH", kwhPoolAddress);
        updatePrice("NCD", ncdPoolAddress);
        updatePrice("KTW", ktwPoolAddress);
        updatePrice("KIA", kiaPoolAddress);
        updatePrice("SSL", sslPoolAddress);
        updatePrice("HWE", hwePoolAddress);
    }

    @Transactional
    protected void updatePrice(String tokenName, String poolAddress) {
        try {
            LiquidityPool pool = LiquidityPool.load(
                    poolAddress,
                    web3j,
                    contractUtil.getTransactionManager(),
                    contractUtil.getContractGasProvider()
            );

            var reserves = pool.getReserves().send();
            BigInteger btcReserve = reserves.component1();
            BigInteger fanTokenReserve = reserves.component2();

            if (fanTokenReserve.equals(BigInteger.ZERO)) {
                log.warn("[{}] 팬토큰 수량 0 → 가격 동기화 생략", tokenName);
                return;
            }

            BigDecimal price = new BigDecimal(btcReserve)
                    .divide(new BigDecimal(fanTokenReserve), 8, BigDecimal.ROUND_HALF_UP);

            TokenPrice tokenPrice = TokenPrice.builder()
                    .tokenName(tokenName)
                    .price(price)
                    .updatedAt(LocalDateTime.now())
                    .build();

            tokenPriceRepository.save(tokenPrice);

            log.info("Price Sync completed: {} = {} BTC", tokenName, price);

        } catch (Exception e) {
            log.error("Price Sync failed: {}", tokenName, e);
        }
    }
}
