package com.github.hallwong.sessions.beanvalidator.dto.validators;

import com.github.hallwong.sessions.beanvalidator.dto.constraints.AscendingCollectionConstraint;
import java.util.Collection;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AscendingCollectionValidator implements
    ConstraintValidator<AscendingCollectionConstraint, Collection> {

  @Override
  public boolean isValid(Collection value, ConstraintValidatorContext context) {
    if (value == null || value.size() < 2) {
      return true;
    }
    Comparable lastElement = null;
    for (Object element : value) {
      if (lastElement == null) {
        if (!Comparable.class.isAssignableFrom(element.getClass())) {
          throw new IllegalStateException(
              "element class " + element.getClass().getName() + " is not comparable!");
        }
        lastElement = (Comparable) element;
      } else if (lastElement.compareTo(element) > 0) {
        return false;
      }
    }
    return true;
  }

}
