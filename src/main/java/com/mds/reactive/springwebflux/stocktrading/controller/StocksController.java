package com.mds.reactive.springwebflux.stocktrading.controller;


import com.mds.reactive.springwebflux.stocktrading.dto.StockRequest;
import com.mds.reactive.springwebflux.stocktrading.dto.StockResponse;
import com.mds.reactive.springwebflux.stocktrading.service.StocksService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
public class StocksController {

    private final StocksService stocksService;

    @GetMapping("/{id}")
    public Mono<StockResponse> getOneStock(@PathVariable String id, @RequestParam(required = false) String currency) {
        return stocksService.getOneStock(id, currency);
    }

    @GetMapping
    public Flux<StockResponse> getAllStocks(@RequestParam(required = false, defaultValue = "0") BigDecimal priceGreaterThan) {
        return stocksService.getAllStocks(priceGreaterThan);
    }

    @PostMapping
    public Mono<StockResponse> createNewStock(@RequestBody StockRequest stockRequest) {
        return stocksService.createNewStock(stockRequest);
    }
}
