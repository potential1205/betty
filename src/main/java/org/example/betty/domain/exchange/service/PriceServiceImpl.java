package org.example.betty.domain.exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.contract.ExchangeView;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PriceServiceImpl implements PriceService {

    private final Web3j web3j;
    private final Web3ContractUtil contractUtil;
    private final TokenRepository tokenRepository;
    private final TokenPriceRepository tokenPriceRepository;

    @Value("${EXCHANGE_VIEW_ADDRESS}")
    private String exchangeViewAddress;

    @Transactional
    @Override
    public void syncAllPrices() {
        updatePrice("DSB");
        updatePrice("SSG");
        updatePrice("LGT");
        updatePrice("LTG");
        updatePrice("KWH");
        updatePrice("NCD");
        updatePrice("KTW");
        updatePrice("KIA");
        updatePrice("SSL");
        updatePrice("HWE");
    }

    @Transactional
    protected void updatePrice(String tokenName) {
        try {
            // ExchangeView 컨트랙트 로드
            ExchangeView exchangeView = ExchangeView.load(
                    exchangeViewAddress,
                    web3j,
                    contractUtil.newReadOnlyTransactionManager(),
                    contractUtil.getContractGasProvider()
            );

            // 가격 조회 (1 팬토큰에 대한 BETTY 수량 반환)
            BigInteger rawPrice = exchangeView.getTokenPrice(tokenName).send();

            if (rawPrice == null || rawPrice.equals(BigInteger.ZERO)) {
                //log.warn("[{}] 가격 0 → 동기화 생략", tokenName);
                return;
            }

            // 단위 변환: wei → BET (1e18 기준)
            BigDecimal price = new BigDecimal(rawPrice)
                    .divide(new BigDecimal("1000000000000000000"), 8, RoundingMode.HALF_UP);

            // 토큰 정보 조회
            Token token = tokenRepository.findByTokenName(tokenName)
                    .orElseThrow(() -> new IllegalArgumentException("Token not found: " + tokenName));

            // 가격 저장
            TokenPrice tokenPrice = TokenPrice.builder()
                    .token(token)
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

    @Override
    public BigDecimal getPriceByTokenName(String tokenName) {
        // 가장 최근의 TokenPrice를 가져오기 위해 findFirstByTokenNameOrderByUpdatedAtDesc 사용
        Optional<TokenPrice> tokenPriceOptional = tokenPriceRepository.findFirstByTokenNameOrderByUpdatedAtDesc(tokenName);

        // 가격이 있으면 반환, 없으면 BigDecimal.ZERO 반환
        return tokenPriceOptional.map(TokenPrice::getPrice).orElse(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal getPriceByTokenId(Long tokenId) {
        // tokenId에 해당하는 Token을 DB에서 조회
        Optional<Token> tokenOptional = tokenRepository.findById(tokenId);

        if (tokenOptional.isEmpty()) {
            // 해당 tokenId에 맞는 Token이 없으면 BigDecimal.ZERO 반환
            return BigDecimal.ZERO;
        }

        Token token = tokenOptional.get();

        // 가장 최근의 TokenPrice를 가져오기 위해 findAllByTokenOrderByUpdatedAtDesc 사용
        Optional<TokenPrice> tokenPriceOptional = tokenPriceRepository.findAllByTokenOrderByUpdatedAtDesc(token).stream().findFirst();

        // 가격이 있으면 반환, 없으면 BigDecimal.ZERO 반환
        return tokenPriceOptional.map(TokenPrice::getPrice).orElse(BigDecimal.ZERO);
    }







}
