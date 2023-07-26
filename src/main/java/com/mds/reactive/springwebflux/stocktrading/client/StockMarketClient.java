package com.mds.reactive.springwebflux.stocktrading.client;

import com.mds.reactive.springwebflux.stocktrading.dto.currencyrate.CurrencyRate;
import com.mds.reactive.springwebflux.stocktrading.dto.stockpublish.StockPublishRequest;
import com.mds.reactive.springwebflux.stocktrading.dto.stockpublish.StockPublishResponse;
import com.mds.reactive.springwebflux.stocktrading.exception.StockCreationException;
import lombok.extern.apachecommons.CommonsLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
public class StockMarketClient {

    private WebClient webClient;

    public StockMarketClient(@Value("${clients.stockMarket.baseUrl}") String baseUrl) {

        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .filter(ExchangeFilterFunction.ofRequestProcessor(
                        request -> Mono.just(ClientRequest.from(request)
                                .header("X-Trace-Id", UUID.randomUUID().toString())
                                .build())
                ))
                .build();
    }

    public Flux<CurrencyRate> getCurrencyRates() {
        return webClient.get()
                .uri("/currencyRates")
                .retrieve()
                .bodyToFlux(CurrencyRate.class)
                .doFirst(() -> log.info("Calling GET Currency Rates API"))
                .doOnNext(currencyRate -> log.info("GET Currency Rates API Response: {}", currencyRate));
    }

    public Mono<StockPublishResponse> publishStock(StockPublishRequest stockPublishRequest) {
        return webClient.post()
                .uri("/stocks/publish")
                .body(BodyInserters.fromValue(stockPublishRequest))
//                .retrieve()
//                .bodyToMono(StockPublishResponse.class)
                .exchangeToMono(response ->
                        !response.statusCode().isError() ? response.bodyToMono(StockPublishResponse.class) :
                                response.bodyToMono(ProblemDetail.class)
                                        .flatMap(problemDetail -> Mono.error(new StockCreationException(problemDetail.getDetail()))))
                .doFirst(() -> log.info("Calling Publish Stock API"))
                .doOnNext(spr -> log.info("Publish Stock API Response: {}", spr));
    }

}
