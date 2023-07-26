package com.mds.reactive.springwebflux.stocktrading.dto.currencyrate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyRate {

    private String currencyName;
    private BigDecimal rate;
}
