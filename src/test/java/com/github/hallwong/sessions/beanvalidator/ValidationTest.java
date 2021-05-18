package com.github.hallwong.sessions.beanvalidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.github.hallwong.sessions.beanvalidator.dto.constraints.groups.AssetCreateAdmin;
import com.github.hallwong.sessions.beanvalidator.dto.request.AssetCreateRequest;
import com.github.hallwong.sessions.beanvalidator.service.AssetService;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;
import javax.validation.groups.Default;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ValidationTest {

  private ValidatorFactory validatorFactory;
  private Validator validator;
  private AssetService assetService;
  private Method createMethod;
  private Class<?>[] groups;

  @BeforeEach
  void setup() throws NoSuchMethodException {
    ResourceBundleMessageInterpolator violationMessageInterpolator =
        new ResourceBundleMessageInterpolator(new PlatformResourceBundleLocator("violation"));
    validatorFactory = Validation.byDefaultProvider().configure()
        .messageInterpolator(violationMessageInterpolator)
        .buildValidatorFactory();
    validator = validatorFactory.getValidator();

    assetService = new AssetService();
    createMethod = AssetService.class.getDeclaredMethod("create", AssetCreateRequest.class);

    groups = new Class<?>[]{AssetCreateAdmin.class, Default.class};
  }

  @AfterEach
  void destroy() {
    validatorFactory.close();
  }

  @Test
  void validate_bean_example() {
    AssetCreateRequest request = new AssetCreateRequest();
    request.setEffectiveDate(LocalDate.of(2020, 12, 21));
    request.setExpirationDate(LocalDate.of(2011, 12, 21));
    Set<ConstraintViolation<AssetCreateRequest>> violations = validator.validate(request, groups);

    assertEquals(3, violations.size());
    List<ConstraintViolation<AssetCreateRequest>> sorted = violations.stream()
        .sorted(Comparator.comparing(v -> v.getPropertyPath().toString()))
        .collect(Collectors.toList());
    ConstraintViolation<AssetCreateRequest> effDateViolation = sorted.get(0);
    assertNotNull(effDateViolation);
    assertEquals("", effDateViolation.getPropertyPath().toString());
    assertEquals("过期时间[2011-12-21]不能在生效时间[2020-12-21]之前。", effDateViolation.getMessage());
    ConstraintViolation<AssetCreateRequest> itemsViolation = sorted.get(1);
    assertNotNull(itemsViolation);
    assertEquals("items", itemsViolation.getPropertyPath().toString());
    assertEquals("不能为空", itemsViolation.getMessage());
    ConstraintViolation<AssetCreateRequest> keyDateViolation = sorted.get(2);
    assertNotNull(keyDateViolation);
    assertEquals("key", keyDateViolation.getPropertyPath().toString());
    assertEquals("不能为null", keyDateViolation.getMessage());
  }

  @Test
  void validate_bean_property_example() {
    AssetCreateRequest request = new AssetCreateRequest();
    request.setEffectiveDate(LocalDate.of(2020, 12, 21));
    Set<ConstraintViolation<AssetCreateRequest>> violations = validator
        .validateProperty(request, "effectiveDate", groups);

    assertEquals(0, violations.size());
  }

  @Test
  void validate_bean_property_example_2() {
    String key = "T-123";
    AssetCreateRequest request = new AssetCreateRequest();
    request.setKey(key);
    Set<ConstraintViolation<AssetCreateRequest>> violations = validator
        .validateProperty(request, "key", groups);

    assertEquals(1, violations.size());
    ConstraintViolation<AssetCreateRequest> violation = violations.stream().findFirst()
        .orElse(null);
    assertNotNull(violation);
    assertEquals("key", violation.getPropertyPath().toString());
    assertEquals("The asset key is invalid.", violation.getMessage());
  }

  @Test
  void validate_value_for_property_of_bean_example() {
    String key = "T-123";
    Set<ConstraintViolation<AssetCreateRequest>> violations = validator
        .validateValue(AssetCreateRequest.class, "key", key, groups);

    assertEquals(1, violations.size());
    ConstraintViolation<AssetCreateRequest> violation = violations.stream().findFirst()
        .orElse(null);
    assertNotNull(violation);
    assertEquals("key", violation.getPropertyPath().toString());
    assertEquals("The asset key is invalid.", violation.getMessage());
  }

  // Bean Validation 1.1 引入的新特性，对方法和构造器的校验

  @Test
  void validate_method_parameter_example() {
    AssetCreateRequest request = new AssetCreateRequest();
    request.setEffectiveDate(LocalDate.of(2020, 12, 21));
    request.setExpirationDate(LocalDate.of(2011, 12, 21));
    ExecutableValidator execVal = validator.forExecutables();
    Set<ConstraintViolation<AssetService>> violations = execVal
        .validateParameters(assetService, createMethod, new Object[]{request}, groups);

    assertEquals(3, violations.size());
    List<ConstraintViolation<AssetService>> sorted = violations.stream()
        .sorted(Comparator.comparing(v -> v.getPropertyPath().toString()))
        .collect(Collectors.toList());
    ConstraintViolation<AssetService> effDateViolation = sorted.get(0);
    assertNotNull(effDateViolation);
    assertEquals("create.arg0", effDateViolation.getPropertyPath().toString());
    assertEquals("过期时间[2011-12-21]不能在生效时间[2020-12-21]之前。", effDateViolation.getMessage());
    ConstraintViolation<AssetService> itemsViolation = sorted.get(1);
    assertNotNull(itemsViolation);
    assertEquals("create.arg0.items", itemsViolation.getPropertyPath().toString());
    assertEquals("不能为空", itemsViolation.getMessage());
    ConstraintViolation<AssetService> keyDateViolation = sorted.get(2);
    assertNotNull(keyDateViolation);
    assertEquals("create.arg0.key", keyDateViolation.getPropertyPath().toString());
    assertEquals("不能为null", keyDateViolation.getMessage());
  }

  @Test
  void validate_method_return_value_example()
      throws InvocationTargetException, IllegalAccessException {
    AssetCreateRequest request = new AssetCreateRequest();
    request.setEffectiveDate(LocalDate.of(2020, 12, 21));
    ExecutableValidator execVal = validator.forExecutables();
    Object returnValue = createMethod.invoke(assetService, request);
    Set<ConstraintViolation<AssetService>> violations = execVal
        .validateReturnValue(assetService, createMethod, returnValue, groups);

    assertEquals(1, violations.size());
    ConstraintViolation<AssetService> violation = violations.stream().findFirst().orElse(null);
    assertNotNull(violation);
    assertEquals("create.<return value>.expirationDate", violation.getPropertyPath().toString());
    assertEquals("不能为null", violation.getMessage());
  }

  @Test
  void fail_fast_mode_example() {
    validatorFactory = Validation.byDefaultProvider().configure()
        .addProperty(HibernateValidatorConfiguration.FAIL_FAST, "true")
        .buildValidatorFactory();
    validator = validatorFactory.getValidator();

    AssetCreateRequest request = new AssetCreateRequest();
    request.setEffectiveDate(LocalDate.of(2020, 12, 21));
    request.setExpirationDate(LocalDate.of(2011, 12, 21));
    Set<ConstraintViolation<AssetCreateRequest>> violations = validator.validate(request, groups);

    assertEquals(1, violations.size());
    ConstraintViolation<AssetCreateRequest> violation = violations
        .stream().findFirst().orElse(null);
    assertNotNull(violation);
  }

}
