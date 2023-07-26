package com.mds.reactive.springwebflux.stocktrading.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class StockCreationException extends RuntimeException{

    public StockCreationException(String message) {
        super(message);
    }

    public ProblemDetail asProblemDetail() {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, getMessage());
        problemDetail.setTitle("Unable to create Stock!!!");
        return problemDetail;
    }

}
