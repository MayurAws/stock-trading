package com.mds.reactive.springwebflux.stocktrading.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mds.reactive.springwebflux.stocktrading.model.Stock;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockRequest {
    @JsonProperty("stockName")
    private String name;
    private BigDecimal price;
    private String currency;

    public Stock toModel() {
        return Stock.builder()
                .name(this.name)
                .price(this.price)
                .currency(this.currency)
                .build();
    }
}
