package com.github.hallwong.sessions.beanvalidator.validation;

import com.github.hallwong.sessions.beanvalidator.dto.constraints.groups.AssetCreateAdmin;
import com.github.hallwong.sessions.beanvalidator.dto.constraints.groups.AssetCreateUser;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.beanvalidation.MethodValidationInterceptor;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Slf4j
public class CustomMethodValidationInterceptor extends MethodValidationInterceptor {

  public CustomMethodValidationInterceptor() {
    this(Validation.buildDefaultValidatorFactory().getValidator());
  }

  public CustomMethodValidationInterceptor(Validator validator) {
    super(validator);
  }

  @Override
  protected Class<?>[] determineValidationGroups(MethodInvocation invocation) {
    HttpServletRequest request = (HttpServletRequest) RequestContextHolder
        .currentRequestAttributes()
        .resolveReference(RequestAttributes.REFERENCE_REQUEST);
    if (request == null) {
      return new Class<?>[0];
    }
    String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
    if ("admin".equalsIgnoreCase(auth)) {
      return new Class<?>[]{AssetCreateAdmin.class, Default.class};
    } else if ("user".equalsIgnoreCase(auth)) {
      return new Class<?>[]{AssetCreateUser.class, Default.class};
    } else {
      return new Class<?>[0];
    }
  }

}