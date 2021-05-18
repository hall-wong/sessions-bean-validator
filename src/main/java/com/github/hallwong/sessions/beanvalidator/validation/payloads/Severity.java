package com.github.hallwong.sessions.beanvalidator.validation.payloads;

import javax.validation.Payload;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Severity {

  public static final class Critical implements Payload {

  }

}
