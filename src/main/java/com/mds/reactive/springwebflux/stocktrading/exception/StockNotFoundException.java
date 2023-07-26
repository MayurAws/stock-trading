package com.mds.reactive.springwebflux.stocktrading.exception;

public class StockNotFoundException extends RuntimeException{

    public StockNotFoundException(String message) {
        super(message);
    }
}
