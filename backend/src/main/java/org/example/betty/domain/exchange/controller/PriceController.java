package org.example.betty.domain.exchange.controller;

import lombok.RequiredArgsConstructor;
import org.example.betty.common.resp.BaseResponse;
import org.example.betty.common.resp.PriceResponse;
import org.example.betty.domain.exchange.dto.resp.TokenPriceResponse;
import org.example.betty.domain.exchange.entity.TokenPrice;
import org.example.betty.domain.exchange.service.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/prices")
public class PriceController {

    private final PriceService priceService;

    @Autowired
    public PriceController(PriceService priceService) {
        this.priceService = priceService;
    }

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

    @GetMapping("/all")
    public ResponseEntity<List<TokenPrice>> getAllTokenPrices() {
        List<TokenPrice> tokenPrices = priceService.getAllTokenPrices();
        if (tokenPrices.isEmpty()) {
            return ResponseEntity.noContent().build(); // 가격 데이터가 없으면 204 No Content 응답
        }
        return ResponseEntity.ok(tokenPrices); // 가격 데이터가 있으면 200 OK 응답
    }


//    // 일간 가격 조회 (자정 가격)
//    @GetMapping("/api/v1/prices/daily/{tokenId}")
//    public PriceResponse getDailyTokenPrice(@PathVariable("tokenId") Long tokenId) {
//        BigDecimal price = priceService.getDailyTokenPrice(tokenId);
//        return PriceResponse.success(price);
//    }
//
//    // 시간별 가격 조회 (정시 가격)
//    @GetMapping("/api/v1/prices/hourly/{tokenId}")
//    public PriceResponse getHourlyTokenPrice(@PathVariable("tokenId") Long tokenId) {
//        BigDecimal price = priceService.getHourlyTokenPrice(tokenId);
//        return PriceResponse.success(price);
//    }

}
