package org.example.betty.domain.exchange.service;

import org.example.betty.domain.exchange.entity.HourlyTokenPrice;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface HourlyPriceService {

    void syncHourlyPrices();

    List<HourlyTokenPrice> getHourlyPricesByTokenId(Long tokenId, LocalDateTime startOfHour, LocalDateTime endOfHour);

    List<HourlyTokenPrice> getDailyPricesByTokenId(Long tokenId);

}
