package com.mds.reactive.springwebflux.stocktrading.dto.stockpublish;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockPublishRequest {

    private String stockName;
    private BigDecimal price;
    private String currencyName;
}
