package com.github.hallwong.sessions.beanvalidator.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotValidDoubleException extends RuntimeException {

  @Override
  public String getMessage() {
    return "Only 2 digits are allowed after the decimal point.";
  }

}
