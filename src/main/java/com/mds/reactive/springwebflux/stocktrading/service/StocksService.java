package com.mds.reactive.springwebflux.stocktrading.service;

import com.mds.reactive.springwebflux.stocktrading.client.StockMarketClient;
import com.mds.reactive.springwebflux.stocktrading.dto.StockRequest;
import com.mds.reactive.springwebflux.stocktrading.dto.StockResponse;
import com.mds.reactive.springwebflux.stocktrading.dto.stockpublish.StockPublishRequest;
import com.mds.reactive.springwebflux.stocktrading.exception.StockCreationException;
import com.mds.reactive.springwebflux.stocktrading.exception.StockNotFoundException;
import com.mds.reactive.springwebflux.stocktrading.repository.StocksRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class StocksService {

    private final StocksRepository stocksRepository;

    private final StockMarketClient stockMarketClient;

    public Mono<StockResponse> getOneStock(String id, String currency) {
        return stocksRepository.findById(id)
                .flatMap(stock -> stockMarketClient.getCurrencyRates()
                        .filter(currencyRate -> currency.equalsIgnoreCase(currencyRate.getCurrencyName()))
                        .singleOrEmpty()
                        .map(currencyRate -> StockResponse.builder()
                                .id(stock.getId())
                                .name(stock.getName())
                                .currency(currencyRate.getCurrencyName())
                                .price(stock.getPrice().multiply(currencyRate.getRate()))
                                .build()))
//                .map(StockResponse::fromModel)
                .switchIfEmpty(Mono.error(new StockNotFoundException("Stock not found with the id: " + id)))
                .doFirst(() -> log.info("Retrieving stock with id: {}", id))
                .doOnNext(stock -> log.info("Stock found: {}", stock))
                .doOnError(ex -> log.error("Something went wrong while retrieving the stock with id: {}", id, ex))
                .doOnTerminate(() -> log.info("Finalized retrieving stock"))
                .doFinally(signalType -> log.info("Finalized retrieving stock with signal type: {}", signalType));
    }

    public Flux<StockResponse> getAllStocks(BigDecimal priceGreaterThan) {
        return stocksRepository.findAll()
                .filter(stock -> stock.getPrice().compareTo(priceGreaterThan) > 0)
                .map(StockResponse::fromModel)
                .doFirst(() -> log.info("Retrieving all stocks"))
                .doOnNext(stock -> log.info("Stock found: {}", stock))
                .doOnError(ex -> log.error("Something went wrong while retrieving the stocks", ex))
                .doOnTerminate(() -> log.info("Finalized retrieving stock"))
                .doFinally(signalType -> log.info("Finalized retrieving stock with signal type: {}", signalType));
    }

    @Transactional
    public Mono<StockResponse> createNewStock(StockRequest stockRequest) {
        return Mono.just(stockRequest)
                .map(StockRequest::toModel)
                .flatMap(stocksRepository::save)
                .flatMap(stock -> stockMarketClient.publishStock(generateStockPublishRequest(stockRequest))
                        .filter(stockPublishResponse -> "SUCCESS".equalsIgnoreCase(stockPublishResponse.getStatus()))
                        .map(stockPublishResponse -> StockResponse.fromModel(stock)))
                .switchIfEmpty(Mono.error(new StockCreationException("Unable to publish Stock publish Request")))
//                .onErrorResume(ex -> {
//                    log.warn("Exception thrown while creating a new stock: ", ex);
//                    return Mono.just(StockResponse.builder().build());
//                })
//                .onErrorReturn(StockResponse.builder().build())
                .onErrorMap(ex -> new StockCreationException(ex.getMessage()));
    }

    private StockPublishRequest generateStockPublishRequest(StockRequest stockRequest) {
        return StockPublishRequest.builder()
                .stockName(stockRequest.getName())
                .price(stockRequest.getPrice())
                .currencyName(stockRequest.getCurrency())
                .build();
    }


}
