package org.example.betty.domain.exchange.repository;

import org.example.betty.domain.exchange.entity.HourlyTokenPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface HourlyTokenPriceRepository extends JpaRepository<HourlyTokenPrice, Long> {
    List<HourlyTokenPrice> findByTokenIdAndUpdatedAtBetween(Long tokenId, LocalDateTime startOfHour, LocalDateTime endOfHour);
}
