package com.mds.reactive.springwebflux.stocktrading.repository;

import com.mds.reactive.springwebflux.stocktrading.dto.StockRequest;
import com.mds.reactive.springwebflux.stocktrading.model.Stock;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StocksRepository extends ReactiveMongoRepository<Stock, String> {
}
