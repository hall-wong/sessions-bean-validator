package com.github.hallwong.sessions.beanvalidator.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnsortedItemsException extends RuntimeException {

  @Override
  public String getMessage() {
    return "The items are not sorted.";
  }

}
