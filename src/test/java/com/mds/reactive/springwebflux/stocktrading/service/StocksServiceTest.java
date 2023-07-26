package com.mds.reactive.springwebflux.stocktrading.service;

import com.mds.reactive.springwebflux.stocktrading.client.StockMarketClient;
import com.mds.reactive.springwebflux.stocktrading.dto.StockRequest;
import com.mds.reactive.springwebflux.stocktrading.dto.stockpublish.StockPublishRequest;
import com.mds.reactive.springwebflux.stocktrading.dto.stockpublish.StockPublishResponse;
import com.mds.reactive.springwebflux.stocktrading.exception.StockCreationException;
import com.mds.reactive.springwebflux.stocktrading.model.Stock;
import com.mds.reactive.springwebflux.stocktrading.repository.StocksRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class StocksServiceTest {

    private static final String STOCK_ID = "123232322415142323235";
    private static final String STOCK_NAME = "SFTUYTTIIIU";
    private static final BigDecimal STOCK_PRICE = BigDecimal.TEN;
    private static final String STOCK_CURRENCY = "INR";

    @Mock
    private StocksRepository stocksRepository;

    @Mock
    private StockMarketClient stockMarketClient;

    @InjectMocks
    private StocksService stocksService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateStock() {
        StockRequest stockRequest = StockRequest.builder()
                .name(STOCK_NAME)
                .price(STOCK_PRICE)
                .currency(STOCK_CURRENCY)
                .build();

        Stock stock = Stock.builder()
                .id(STOCK_ID)
                .name(STOCK_NAME)
                .price(STOCK_PRICE)
                .currency(STOCK_CURRENCY)
                .build();

        StockPublishResponse stockPublishResponse = StockPublishResponse.builder()
                        .stockName(STOCK_NAME)
                                .price(STOCK_PRICE)
                                        .currencyName(STOCK_CURRENCY)
                                                .status("SUCCESS")
                                                        .build();

        when(stocksRepository.save(any())).thenReturn(Mono.just(stock));
        when(stockMarketClient.publishStock(any(StockPublishRequest.class))).thenReturn(Mono.just(stockPublishResponse));

        StepVerifier.create(stocksService.createNewStock(stockRequest))
                .assertNext(stockResponse -> {
                    assertNotNull(stockResponse);
                    assertEquals(STOCK_ID, stockResponse.getId());
                    assertEquals(STOCK_NAME, stockResponse.getName());
                    assertEquals(STOCK_CURRENCY, stockResponse.getCurrency());
                    assertEquals(STOCK_PRICE, stockResponse.getPrice());
                })
                .verifyComplete();
    }


    @Test
    void shouldThrowStockCreationExceptionWhenUnableToSave() {
        StockRequest stockRequest = StockRequest.builder()
                .name(STOCK_NAME)
                .price(STOCK_PRICE)
                .currency(STOCK_CURRENCY)
                .build();

        when(stocksRepository.save(any())).thenThrow(new RuntimeException("Connection Lost!!!"));

        StepVerifier.create(stocksService.createNewStock(stockRequest))
                .verifyError(StockCreationException.class);
    }

    @Test
    void shouldThrowStockCreationExceptionWhenStockMarketFailed() {
        StockRequest stockRequest = StockRequest.builder()
                .name(STOCK_NAME)
                .price(STOCK_PRICE)
                .currency(STOCK_CURRENCY)
                .build();

        Stock stock = Stock.builder()
                .id(STOCK_ID)
                .name(STOCK_NAME)
                .price(STOCK_PRICE)
                .currency(STOCK_CURRENCY)
                .build();

        StockPublishResponse stockPublishResponse = StockPublishResponse.builder()
                .stockName(STOCK_NAME)
                .price(STOCK_PRICE)
                .currencyName(STOCK_CURRENCY)
                .status("FAIL")
                .build();

        when(stocksRepository.save(any())).thenReturn(Mono.just(stock));
        when(stockMarketClient.publishStock(any(StockPublishRequest.class))).thenReturn(Mono.just(stockPublishResponse));


        when(stocksRepository.save(any())).thenThrow(new RuntimeException("Connection Lost!!!"));

        StepVerifier.create(stocksService.createNewStock(stockRequest))
                .verifyError(StockCreationException.class);
    }

}
