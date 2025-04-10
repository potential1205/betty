package org.example.betty.domain.exchange.repository;


import org.example.betty.domain.exchange.entity.Token;
import org.example.betty.domain.exchange.entity.TokenPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TokenPriceRepository extends JpaRepository<TokenPrice, Long> {
    List<TokenPrice> findAllByTokenOrderByUpdatedAtDesc(Token token);

    Optional<TokenPrice> findByTokenName(String tokenName);

    Optional<TokenPrice> findFirstByTokenNameOrderByUpdatedAtDesc(String tokenName);

//    // 자정 가격을 가져오는 메소드
//    List<TokenPrice> findByTokenIdAndUpdatedAtBetween(Long tokenId, LocalDateTime startOfDay, LocalDateTime endOfDay);
//
//    // 정시 가격을 가져오는 메소드
//    List<TokenPrice> findByTokenIdAndUpdatedAtBetweenHourly(Long tokenId, LocalDateTime startOfHour, LocalDateTime endOfHour);
//
//    // 자정 가격을 가져오는 메서드 (과거 자정 가격)
//    List<TokenPrice> findAllByTokenIdAndUpdatedAtBefore(Long tokenId, LocalDateTime startOfDay);
//
//    // 정시 가격을 가져오는 메서드 (과거 정시 가격)
//    List<TokenPrice> findAllByTokenIdAndUpdatedAtBeforeHourly(Long tokenId, LocalDateTime startOfHour);

}
