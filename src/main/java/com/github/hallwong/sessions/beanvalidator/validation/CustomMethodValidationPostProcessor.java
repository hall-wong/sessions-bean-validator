package com.github.hallwong.sessions.beanvalidator.validation;

import java.util.stream.Stream;
import javax.validation.Validator;
import org.aopalliance.aop.Advice;
import org.springframework.boot.validation.beanvalidation.FilteredMethodValidationPostProcessor;
import org.springframework.boot.validation.beanvalidation.MethodValidationExcludeFilter;

public class CustomMethodValidationPostProcessor extends FilteredMethodValidationPostProcessor {

  public CustomMethodValidationPostProcessor(Stream<MethodValidationExcludeFilter> orderedStream) {
    super(orderedStream);
  }

  @Override
  protected Advice createMethodValidationAdvice(Validator validator) {
    return validator != null ?
        new CustomMethodValidationInterceptor(validator) : new CustomMethodValidationInterceptor();
  }

}
