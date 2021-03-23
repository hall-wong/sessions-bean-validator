package com.github.hallwong.sessions.beanvalidator.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NullAssetKeyException extends RuntimeException {

  @Override
  public String getMessage() {
    return "The asset key must not be null.";
  }

}
