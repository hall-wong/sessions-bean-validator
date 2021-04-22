## 继续我们的工作

书接上文，在`AssetCreateRequest`中，属性`key`的校验规则不仅不能为空，还需要满足正则表达式：`(DSC-\d{4}|OPT-\d{5})`。Bean Validation针对正则表达式提供了内置的约束：`Pattern`，把它添加到属性`key`上：

```java
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

// ... annotations
public class AssetCreateRequest {

  @NotNull(message = "The asset key must not be null.")
  @Pattern(regexp = "(DSC-\\d{4}|OPT-\\d{5})", message = "The asset key is invalid.")
  private String key;
  
  // ... other fields

}
```

运行测试，观察测试用例`when_create_asset_should_return_bad_request_given_null_key`中violations有几条信息。

###思考

为什么当`key`为空时，约束`Pattern`会失效？

## Message参数

在每一个约束中，都会有一个字符串类型的`message`参数，用于覆盖默认的消息。这个参数可以接受字符串常量，也可以接受表达式。对于字符串常量，Bean Validation会直接展示；对于表达式，Bean Validation会在其国际化上下文中搜索相应的结果并渲染，这部分的内容将会在后续的章节中出现。

## 为什么约束会失效

这就不得不提到单一职责原则（[SPR](https://en.wikipedia.org/wiki/Single-responsibility_principle)）了，一个约束应该负责校验的逻辑应该是单一的。以约束`@Pattern` 为例，一个字符串是否为空，更应该由约束`@NotNull`来决定，而不是交给约束`@Pattern` 来决定。所以在验证约束`@Pattern` ，遇到了参数为`null`，那么应该返回校验通过，非空校验交给约束`@NotNull`来处理。

同样的，如果两个约束同时不被满足，那么应该同时抛出这些违例（violation）信息，而不是只抛出一个。（出于安全考虑，有些场景可能会将这个特性关闭，防止黑客试错。可以参考下这个官方文档：[Fail fast mode](https://docs.jboss.org/hibernate/validator/4.2/reference/en-US/html/validator-specifics.html#d0e3142)）

### 小败笔

对于我们开发来讲，我们会经常判断一个字符串变量是否为空（emtpy），而字符串在Java中是一个引用类型，这就意味着字符串可能为`null`，也可能为`""`。所以一般的情况下，我们会像Apache下的[commons-lang](http://commons.apache.org/proper/commons-lang/)类库一样，去这样判断字符串是否为空。

```java
public static boolean isEmpty(final CharSequence cs) {
    return cs == null || cs.length() == 0;
}

public static boolean isNotEmpty(final CharSequence cs) {
    return !isEmpty(cs);
}
```

同样的，Bean Validation在实现约束`@NotEmpty`时，认为`null`值的字符串违反了约束。这就导致了一些场景中，我们需要对一个可以为`null`值，但不可以为`""`值的属性进行校验时，内置的约束`@NotEmpty`不满足要求。

同样的事情也发生在约束`@NotBlank`上。

## 本节结束

好了，本节到此结束，本节内容主要是展示了多个约束作用一起时，约束是如何共同作用的。还初识了一个重要的约束参数：`message`。

下一节将会讲到如何定制自己的约束，请切换到`04_customize_your_constraint`分支继续下一节。

