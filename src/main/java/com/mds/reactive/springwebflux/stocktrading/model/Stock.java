package com.mds.reactive.springwebflux.stocktrading.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Stock {
    private String id;
    private String name;
    @NonNull
    private BigDecimal price;
    private String currency;
}
