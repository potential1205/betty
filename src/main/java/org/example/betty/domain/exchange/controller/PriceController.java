package org.example.betty.domain.exchange.controller;

import lombok.RequiredArgsConstructor;
import org.example.betty.common.resp.BaseResponse;
import org.example.betty.common.resp.PriceResponse;
import org.example.betty.domain.exchange.dto.resp.TokenPriceResponse;
import org.example.betty.domain.exchange.entity.TokenPrice;
import org.example.betty.domain.exchange.service.PriceService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/prices")
@RequiredArgsConstructor
public class PriceController {

    private final PriceService priceService;

    @PostMapping("/sync")
    public BaseResponse<Void> syncAllTokenPrices() {
        priceService.syncAllPrices();
        return BaseResponse.success();
    }

    // tokenName을 기반으로 최신 가격 조회 (PriceResponse 반환)
    @GetMapping("/token/{tokenName}")
    public PriceResponse getTokenPriceByTokenName(@PathVariable("tokenName") String tokenName) {
        BigDecimal price = priceService.getPriceByTokenName(tokenName);
        return PriceResponse.success(price);
    }

    // tokenId를 기반으로 최신 가격 조회 (PriceResponse 반환)
    @GetMapping("/tokenid/{tokenId}")
    public PriceResponse getTokenPriceByTokenId(@PathVariable("tokenId") Long tokenId) {
        BigDecimal price = priceService.getPriceByTokenId(tokenId);
        return PriceResponse.success(price);
    }

}
