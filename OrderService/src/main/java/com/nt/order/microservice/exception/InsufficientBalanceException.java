package com.nt.order.microservice.exception;

public class InsufficientBalanceException extends RuntimeException{
  public InsufficientBalanceException(String message) {
    super(message);
  }
}
