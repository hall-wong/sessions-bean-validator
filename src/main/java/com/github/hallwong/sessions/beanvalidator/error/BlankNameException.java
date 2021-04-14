package com.github.hallwong.sessions.beanvalidator.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BlankNameException extends RuntimeException {

  @Override
  public String getMessage() {
    return "The name of the item must not blank.";
  }

}
