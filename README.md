## 新的需求

系统以及运行了一段时间，运维人员发现磁盘很快就被占满了，经过运维组开发的调查，发现是ERROR级别中的校验异常太多了，导致日志被迅速填满。目前运维组已经提交了代码，将校验异常屏蔽了：

```java
  public static class ErrorHandler implements ProblemHandling {

    @Override
    public void log(Throwable throwable, Problem problem, NativeWebRequest request,
        HttpStatus status) {
      if (ConstraintViolationProblem.TYPE.equals(problem.getType())) {
        return;
      }
      log.error("caught an error:", throwable);
    }

  }
```

但同时，安全组的同事希望可以将查询资产接口的校验异常记录下来，因为他们担心有黑客尝试渗透攻击。

## 约束的扩展

在上一节中，我们使用约束中的`groups`参数，对参数进行分组校验。类似的，在实际业务需求中，我们也需要可以通过定义一种类似`groups`的元数据（metadata），这样在约束被违反时，可以通过这种元数据，统一的处理约束异常。Bean Validation约束中的`payload`参数就是做这个事情的。

`payload`参数是一个Class数组，其中的Class必须实现空接口`javax.validation.Payload`。根据新的需求，我们可以定义一个`Payload`类：

```java
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Severity {

  public static final class Critical implements Payload {

  }

}
```

然后根据要求，在`AssetResource#list`方法中，对参数`key`中的约束中，添加上新创建的`Payload`：

```java
public class AssetResource {

  public List<AssetResponse> list(
      @Valid @AssetKeyConstraint(payload = Severity.Critical.class)
      @RequestParam(name = "key", required = false)
          String key) {
    // implementation...
  }
  
}
```

接着在上一节定义的`CustomMethodValidationInterceptor`中，重写`invoke`方法：在`ConstraintViolationException`被抛出时，根据约束定义的`payload`，选择输出异常信息。

```java
@Slf4j
public class CustomMethodValidationInterceptor extends MethodValidationInterceptor {
  //...
  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    try {
      return super.invoke(invocation);
    } catch (ConstraintViolationException e) {
      e.getConstraintViolations().forEach(this::logConstraintViolation);
      throw e;
    }
  }

  private void logConstraintViolation(ConstraintViolation<?> violation) {
    ConstraintDescriptor<?> constraintDescriptor = violation.getConstraintDescriptor();
    Set<Class<? extends Payload>> payloads = constraintDescriptor.getPayload();
    if (payloads.contains(Severity.Critical.class)) {
      log.error("Critical violation: {}", violation.getMessage());
    }
  }

}
```

### 动手练习

运行测试，观察测试`when_list_assets_should_return_bad_request_given_not_valid_key`是否通过。

## 本节结束

好了，本节到此结束，本节内容主要是展示了通过`payload`参数为约束添加元数据扩展，还展示了如何从`ConstraintViolation`中获取约束定义的`payload`，达到业务需求。


下一节将会讲到如何在Java SE环境中使用Bean Validation，请切换到`08_jsr380_in_se`分支继续下一节。








