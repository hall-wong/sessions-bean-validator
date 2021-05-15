package com.github.hallwong.sessions.beanvalidator.dto.constraints;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.github.hallwong.sessions.beanvalidator.dto.validators.AssetKeyValidator;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = AssetKeyValidator.class)
public @interface AssetKeyConstraint {

  String message() default "The asset key is invalid.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
