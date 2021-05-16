## 新的需求

随着业务的扩展，我们系统里身份越来越多，在创建Asset时，会有两种身份：一种是管理员，另一种是普通用户，通过请求头部中的`Authorization`字段里的值进行区分。当普通用户创建Asset时，不可以填入`items`属性，而当是管理员时，则这个字段不能为空（跟之前一致）。

## 按组（Group）校验

很多情况下，我们可能需要对同一个类，甚至同一个接口中的同一个参数按照不同的分组进行校验。很显然，在新的需求中，对于创建资产的接口，会根据用户的身份，分成两个组：管理员组和普通用户组。在校验`AssetCreateRequest`时，对于`items`属性，管理员组要满足`@NotEmpty`约束，普通用户组要满足`@Null`约束。而对于其他属性，还是与之前的校验逻辑一致。

### `groups`属性

我们可以在使用约束注解时，通过`groups`属性，定义这个约束属于哪个组。

```java
public class AssetCreateRequest {

  @NotNull
  @AssetKeyConstraint(groups = CustomGroup.class)
  private String key;
// ...
}
```

在上面的例子中，可以看到`@AssetKeyConstraint`约束是属于`CustomGroup`组的，而`@NotNull`约束没有指定组，那么Bean Validation会为它分配一个默认组：`javax.validation.groups.Default`。那么在校验时，如果没有指明校验组（也是默认使用`javax.validation.groups.Default`）或指明的组中包含`javax.validation.groups.Default`，那么就会检查`key`是否符合`@NotNull`约束。如果的指明的组中不包含`CustomGroup`，那么就不会校验`key`是否符合`@AssetKeyConstraint`约束。

### 创建组

在Bean Validation中，组必须是一个接口，否则抛出`HV000045`异常。

按照新的需求，创建两个组：

```java
public interface AssetCreateAdmin {

}
```

```java
public interface AssetCreateUser {

}
```

### 设置组到约束上

根据业务的需要，对`items`属性上的约束做出如下调整：

```java
public class AssetCreateRequest {
// ...
  @Valid
  @Null(groups = AssetCreateUser.class)
  @NotEmpty(groups = AssetCreateAdmin.class)
  @AscendingCollectionConstraint
  private List<AssetItemCreateRequest> items;

}
```

### 按组校验

虽然Spring提供了`Validated`注解，可以在其中输入分组信息。但是这只能提供静态的按组查询，但往往在实际工作中，我们更需要动态的按组查询，所以为了满足新的需求，我们需要对Spring的Validation AOP组件进行一定的改造。

首先，创建一个`MethodValidationPostProcessor`，放入Spring上下文中。Spring通过`MethodValidationPostProcessor`来达到校验功能，可以参考下`ValidationAutoConfiguration#methodValidationPostProcessor`的实现。

```java
@Configuration
public class ValidationConfig {
// ...
  @Bean
  public static MethodValidationPostProcessor customMethodValidationPostProcessor(
      Environment environment, @Lazy Validator validator,
      ObjectProvider<MethodValidationExcludeFilter> excludeFilters) {
    FilteredMethodValidationPostProcessor processor =
        new CustomMethodValidationPostProcessor(excludeFilters.orderedStream());
    processor.setValidatedAnnotationType(RestController.class);
    boolean proxyTargetClass = environment
        .getProperty("spring.aop.proxy-target-class", Boolean.class, true);
    processor.setProxyTargetClass(proxyTargetClass);
    processor.setValidator(validator);
    return processor;
  }

}
```

*注意：这里的`validationAnnotationType`设置为`RestController`，这意味我们会校验被`@RestController`注解标记的类下的方法，而不是`@Validated`。*

```java
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
```

然后创建一个`MethodValidationInterceptor`，作为Validation AOP的advice：

```java
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
```

在这里，我们只需要重写覆盖掉`determineValidationGroups`方法即可，在Spring的实现中，会从`@Validated`注解中读取组信息，这里我们替换成自己的逻辑：从请求头中读取身份信息并设置组信息。

值得注意的是，当身份类型为管理员或普通用户时，除了要返回对应的组外，还要返回默认的组`Default`，否则校验时将会忽略没有指明组的约束。

### 动手练习

运行测试，测试`when_create_asset_should_return_bad_request_given_not_null_items_and_user_auth`应该是通过。

## 本节结束

好了，本节到此结束，本节内容主要是展示了通过`groups`参数调整校验时的策略，还展示了如何自定义Spring的Validation AOP，以更符合我们的业务需求。


下一节将会讲到约束中另外一个属性`payload`，请切换到`07_payload`分支继续下一节。








