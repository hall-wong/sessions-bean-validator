package com.github.hallwong.sessions.beanvalidator.dto.validators;

import com.github.hallwong.sessions.beanvalidator.dto.constraints.ExpirationDateConstraint;
import com.github.hallwong.sessions.beanvalidator.dto.request.AssetCreateRequest;
import java.time.LocalDate;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExpirationDateValidator implements
    ConstraintValidator<ExpirationDateConstraint, AssetCreateRequest> {

  private boolean allowEqualToEffectiveDate;

  @Override
  public void initialize(ExpirationDateConstraint constraintAnnotation) {
    allowEqualToEffectiveDate = constraintAnnotation.allowEqualToEffectiveDate();
  }

  @Override
  public boolean isValid(AssetCreateRequest value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }
    LocalDate effectiveDate = value.getEffectiveDate();
    if (effectiveDate == null) {
      return true;
    }
    LocalDate expirationDate = value.getExpirationDate();
    if (expirationDate == null) {
      return true;
    }
    return allowEqualToEffectiveDate ?
        !expirationDate.isBefore(effectiveDate) : expirationDate.isAfter(effectiveDate);
  }

}
