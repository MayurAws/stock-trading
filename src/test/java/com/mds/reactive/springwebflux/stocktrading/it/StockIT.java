package com.mds.reactive.springwebflux.stocktrading.it;

import com.mds.reactive.springwebflux.stocktrading.client.StockMarketClient;
import com.mds.reactive.springwebflux.stocktrading.dto.StockResponse;
import com.mds.reactive.springwebflux.stocktrading.dto.currencyrate.CurrencyRate;
import com.mds.reactive.springwebflux.stocktrading.model.Stock;
import com.mds.reactive.springwebflux.stocktrading.repository.StocksRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class StockIT {


    private static final String STOCK_ID = "123232322415142323235";
    private static final String STOCK_NAME = "SFTUYTTIIIU";
    private static final BigDecimal STOCK_PRICE = BigDecimal.TEN;
    private static final String STOCK_CURRENCY = "INR";

    @MockBean
    private StocksRepository stocksRepository;

    @MockBean
    private StockMarketClient stockMarketClient;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldGetOneStock() {
        Stock stock = Stock.builder()
                .id(STOCK_ID)
                .name(STOCK_NAME)
                .price(STOCK_PRICE)
                .currency(STOCK_CURRENCY)
                .build();

        CurrencyRate currencyRate = CurrencyRate.builder()
                .currencyName(STOCK_CURRENCY)
                .rate(BigDecimal.ONE)
                .build();

        when(stocksRepository.findById(STOCK_ID)).thenReturn(Mono.just(stock));
        when(stockMarketClient.getCurrencyRates()).thenReturn(Flux.just(currencyRate));

        StockResponse stockResponse = webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stocks/{id}")
                        .build(STOCK_ID))
                .exchange()
                .expectStatus().isOk()
                .expectBody(StockResponse.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(stockResponse);
        assertEquals(stockResponse.getId(), STOCK_ID);
        assertEquals(stockResponse.getName(), STOCK_NAME);
        assertEquals(stockResponse.getPrice(), STOCK_PRICE);
        assertEquals(stockResponse.getCurrency(), STOCK_CURRENCY);
    }

    @Test
    void shouldReturnNotFoundWhenGetOneStock() {
        when(stocksRepository.findById(STOCK_ID)).thenReturn(Mono.empty());

        ProblemDetail problemDetail = webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/stocks/{id}")
                        .build(STOCK_ID))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ProblemDetail.class)
                .returnResult()
                .getResponseBody();

        assertTrue(problemDetail.getDetail().contains("Stock Not Found!!!"));
    }
}
