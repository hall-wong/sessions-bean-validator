package com.github.hallwong.sessions.beanvalidator.dto.validators;

import com.github.hallwong.sessions.beanvalidator.dto.constraints.AssetKeyConstraint;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AssetKeyValidator implements ConstraintValidator<AssetKeyConstraint, String> {

  private static final Pattern ASSET_KEY_PATTERN = Pattern.compile("(DSC-\\d{4}|OPT-\\d{5})");

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }
    Matcher matcher = ASSET_KEY_PATTERN.matcher(value);
    return matcher.matches();
  }

}
