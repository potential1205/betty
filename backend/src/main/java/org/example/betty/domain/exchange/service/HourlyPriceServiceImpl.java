package org.example.betty.domain.exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.betty.domain.exchange.entity.HourlyTokenPrice;
import org.example.betty.domain.exchange.entity.Token;
import org.example.betty.domain.exchange.repository.HourlyTokenPriceRepository;
import org.example.betty.domain.exchange.repository.TokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HourlyPriceServiceImpl implements HourlyPriceService {

    private final HourlyTokenPriceRepository hourlyTokenPriceRepository;
    private final TokenRepository tokenRepository;
    private final PriceService priceService; // 기존 PriceService 사용하여 가격 조회

    @Transactional
    @Override
    public void syncHourlyPrices() {
        // 1시간마다 각 토큰의 가격을 DB에 저장
        saveHourlyPrice("DSB");
        saveHourlyPrice("SSG");
        saveHourlyPrice("LGT");
        saveHourlyPrice("LTG");
        saveHourlyPrice("KWH");
        saveHourlyPrice("NCD");
        saveHourlyPrice("KTW");
        saveHourlyPrice("KIA");
        saveHourlyPrice("SSL");
        saveHourlyPrice("HWE");
    }

    private void saveHourlyPrice(String tokenName) {
        try {
            // 가격 조회
            BigDecimal price = priceService.getPriceByTokenName(tokenName);

            if (price.compareTo(BigDecimal.ZERO) == 0) {
                log.warn("[{}] 가격 0 → 동기화 생략", tokenName);
                return;
            }

            // Token 정보 조회
            Token token = tokenRepository.findByTokenName(tokenName)
                    .orElseThrow(() -> new IllegalArgumentException("Token not found: " + tokenName));

            // HourlyTokenPrice 저장 (tokenId와 tokenName을 따로 저장)
            HourlyTokenPrice hourlyTokenPrice = HourlyTokenPrice.builder()
                    .token(token)
                    .tokenName(token.getTokenName())  // tokenName 저장
                    .price(price)
                    .updatedAt(LocalDateTime.now())
                    .build();

            hourlyTokenPriceRepository.save(hourlyTokenPrice);

            log.info("[{}] Hourly price saved: {}", tokenName, price);

        } catch (Exception e) {
            log.error("[{}] Hourly price sync failed: {}", tokenName, e.getMessage());
        }
    }

    @Transactional
    @Override
    public List<HourlyTokenPrice> getHourlyPricesByTokenId(Long tokenId, LocalDateTime startOfHour, LocalDateTime endOfHour) {
        // 특정 tokenId와 시간 범위에 해당하는 HourlyTokenPrice 조회
        return hourlyTokenPriceRepository.findByTokenIdAndUpdatedAtBetween(tokenId, startOfHour, endOfHour);
    }

    @Transactional
    @Override
    public List<HourlyTokenPrice> getDailyPricesByTokenId(Long tokenId) {
        // 자정에 해당하는 가격만 조회 (특정 tokenId에 대한 자정 가격)
        LocalDateTime dayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime dayEnd = dayStart.plusDays(1); // 자정부터 1일 후까지

        return hourlyTokenPriceRepository.findByTokenIdAndUpdatedAtBetween(tokenId, dayStart, dayEnd);
    }
}
