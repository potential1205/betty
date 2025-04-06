package org.example.betty.domain.exchange.controller;

import lombok.RequiredArgsConstructor;
import org.example.betty.common.resp.BaseResponse;
import org.example.betty.domain.exchange.service.PriceService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
