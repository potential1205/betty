package org.example.betty.domain.exchange.controller;

import lombok.RequiredArgsConstructor;
import org.example.betty.domain.exchange.entity.HourlyTokenPrice;
import org.example.betty.domain.exchange.service.HourlyPriceServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/prices")
@RequiredArgsConstructor
public class HourlyTokenPriceController {

    private final HourlyPriceServiceImpl hourlyPriceService;

    // 매시 정각의 가격 조회 (tokenId로 조회)
    @GetMapping("/hourly/{tokenId}")
    public List<HourlyTokenPrice> getHourlyTokenPrices(@PathVariable("tokenId") Long tokenId,
                                                       @RequestParam("startOfHour") LocalDateTime startOfHour,
                                                       @RequestParam("endOfHour") LocalDateTime endOfHour) {
        return hourlyPriceService.getHourlyPricesByTokenId(tokenId, startOfHour, endOfHour);
    }

    // 자정의 가격 조회 (tokenId로 조회)
    @GetMapping("/daily/{tokenId}")
    public List<HourlyTokenPrice> getDailyTokenPrices(@PathVariable("tokenId") Long tokenId) {
        return hourlyPriceService.getDailyPricesByTokenId(tokenId);
    }
}
