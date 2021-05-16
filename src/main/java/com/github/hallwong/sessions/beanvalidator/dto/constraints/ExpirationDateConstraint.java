package com.github.hallwong.sessions.beanvalidator.dto.constraints;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.github.hallwong.sessions.beanvalidator.dto.validators.ExpirationDateValidator;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = ExpirationDateValidator.class)
public @interface ExpirationDateConstraint {

  String message() default "{com.github.hallwong.sessions.beanvalidator.dto.constraints.ExpirationDateConstraint.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  boolean allowEqualToEffectiveDate() default false;

}
