package com.github.hallwong.sessions.beanvalidator.conf;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.spring.web.advice.ProblemHandling;

@Configuration
public class ErrorHandlerConfig {

  @Bean
  public ProblemModule problemModule() {
    return new ProblemModule();
  }

  @RestControllerAdvice
  @Slf4j
  public static class ErrorHandler implements ProblemHandling {

    @Override
    public void log(Throwable throwable, Problem problem, NativeWebRequest request,
        HttpStatus status) {
      log.error("caught an error:", throwable);
    }

  }

}
