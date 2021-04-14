package com.github.hallwong.sessions.beanvalidator.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EmptyItemsException extends RuntimeException {

  @Override
  public String getMessage() {
    return "The items must not empty or contain null element.";
  }

}
