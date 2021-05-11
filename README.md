## 继续我们的工作

现在，我已经将大部分校验逻辑改为Bean Validation中的内置约束实现，但是对于一些复杂的校验逻辑，内置的约束没有办法处理，这时候就需要我们自行定义约束来满足校验逻辑。

在Bean Validation中，约束需要搭配**校验器**（Validator）来使用。一个约束由于被校验的数据可能存在不同的数据类型，所以一个约束可以指定至少一个或多个校验器。一个校验器只会校验一种类型是否满足一个约束。

### 自定义约束

在Bean Validation中，约束是以Java注解（annotation）存在的，因此约束可以很方便清晰表述一个POJO的校验逻辑。如果我们想要自定义约束，那么也是从一个简单的注解开始，以内置的`@NotNull`约束为例：

```java
// ... @Target & @Retention
@Constraint(validatedBy = { })
public @interface NotNull {

	String message() default "{javax.validation.constraints.NotNull.message}";

	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };

}
```

可以看到这个`@NotNull`注解被`@Constraint`注解标记了，这表明了`@NotNull`注解是Bean Validation中的一个约束了。`@Constraint`中的`validatedBy`属性指明了这个约束所有的校验器。在`@NotNull`这个例子里，这个列表是空，是为了各个厂商实现，hibernate在[这里](https://github.com/hibernate/hibernate-validator/blob/22620adef18f43f23a1c5d0e1faced68f25af550/engine/src/main/java/org/hibernate/validator/internal/metadata/core/ConstraintHelper.java#L615)做了hack。

同时，一个约束必须要有以下三个属性，否则启动会抛出`HV000074`的错误：

* message：我们上节有说过，当约束不满足时展示的文本信息，hibernate建议配置为类的全路径名加上`.message`，为了方便国际化文本管理，我们在本节将直接输入异常信息。我们会在之后章节中单独讲解国际化；
* groups：用于指定分组信息，默认可以为空。我们会在之后章节中单独讲解；
* payload：便于扩展，默认可以为空。我们会在之后章节中单独讲解；

### 编写校验器

在Bean Validation中，校验器必须是`ConstraintValidator`的实现类，`ConstraintValidator`是一个接口，包含了两个方法，两个泛型：

```java
public interface ConstraintValidator<A extends Annotation, T> {

	default void initialize(A constraintAnnotation) {
	}

	boolean isValid(T value, ConstraintValidatorContext context);
  
}
```

两个方法：

* initialize：根据注解中的属性来初始化校验器，可以不实现；
* isValid：判断数据是否满足约束的逻辑，需要实现；

两个泛型：

* A：约束的注解类型；
* T：被校验数据的类型；

还是以`@NotNull`约束的校验器为例：

```java
public class NotNullValidator implements ConstraintValidator<NotNull, Object> {

	@Override
	public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
		return object != null;
	}

}
```

可以看到在这个校验器里，泛型A为`@NotNull`注解，泛型T为`Object`，也就是任何类型。这个校验器只实现了`isValid`方法。

## 动手练习

在我们的代码中，对于`AssetCreateRequest`中的`expirationDate`属性校验是比较复杂，需要比较`effectiveDate`。我们可以自定一个约束和校验器，来校验`expirationDate`是否满足约束：

```java
@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = ExpirationDateValidator.class)
public @interface ExpirationDateConstraint {

  String message() default "The expiration date must be after effective date.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  boolean allowEqualToEffectiveDate() default false;

}
```

可以看到，在这个约束中，有一个自定义的属性`allowEqualToEffectiveDate`，用于定义`expirationDate`是否可以与`effectiveDate`相等。

```java
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
```

可以看到在校验器初始化的时候，在`initialize`方法中，校验器会读取约束中`allowEqualToEffectiveDate`属性，并保持到自己的属性中。由此，我们可以推定，校验器的生命周期是从每个约束注解解析时创建开始，直到容器被销毁。

而在实现校验器的`isValid`方法时候，如果遇到了`null`值，都会返回校验通过，至于为什么，可以回顾下上节的思考题。

## 本节结束

好了，本节到此结束，本节内容主要是展示了通过`@Constraint`将一个注解转化成Bean Validation的约束，以及必须要定义的属性：`message`、`groups`和`payload`。还展示了约束必须关联的校验器所要实现的接口`ConstraintValidator`。

下一节将会讲到如何国际化文本信息，请切换到`05_text_internationalization`分支继续下一节。

