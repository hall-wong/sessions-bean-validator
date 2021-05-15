## 新的需求

现在，我们已经将所有的校验逻辑都改为Bean Validation来实现。这时候产品经理觉得对于`@ExpirationDateConstraint`约束的文本信息过于简单了，希望可以将`effectiveDate`和`expirationDate`展示在文本中，同时说明是否允许`expirationDate`与`effectiveDate`相同。文本如下：

当不允许相同时：

```
The expirationDate[2011-12-21] should not be early than or equal to the effectiveDate[2020-12-21].
```

当允许相同时：

```
The expirationDate[2011-12-21] should not be early than the effectiveDate[2020-12-21].
```

同时客户希望不同地区的客户端在遇到校验无法通过时，违例信息要用客户端所使用的语言来展示，也就是常说的国际化（Internationalization）。中文文本如下：

当不允许相同时：

```
过期时间[2011-12-21]不能在生效时间[2020-12-21]之前或当天。
```

当允许相同时：

```
过期时间[2011-12-21]不能在生效时间[2020-12-21]之前。
```



## 文本渲染（Message interpolation）

熟悉Java的小伙伴可能会比较清楚，在Java中实现国际化，我们会在需要国际化文本的地方定义一个文本键（message key），当程序运行到这里时，程序会根据上下文中的地区信息`Locale`加载对应地区的资源文件，并按照给定的文本键在该资源文件中搜索对应的文本资源并展示。

但是对于异常信息，比如校验异常，往往我们希望可以获得更多信息，例如被校验值。甚至一些表达式功能，比如当约束中的某个属性为特定值时就展示特定的文本。因此Bean Validation提供了文本渲染的功能来满足此类需求。

*注：interpolation直译为**插值**，是离散数学中的一个概念，指根据两个相邻的元素推导出中间元素的值。为了方便理解，我意译为**渲染**。*

### 文本描述符（Message descriptor）

文本描述符就是之前定义在约束`message`参数中的值，正如之前提到的：

> 这个参数可以接受字符串常量，也可以接受表达式。对于字符串常量，Bean Validation会直接展示；对于表达式，Bean Validation会在其国际化上下文中搜索相应的结果并渲染

文本描述符可以包含零个或多个表达式，表达式是指被`{}`或`${}`嵌套的文本。Bean Validation会文本描述符递归解析直到不再含有表达式。在表达式文本中，`{`、`}`、`$`和`\`是关键字，需要时通过`\`转义。

Bean Validation会优先从上下文和国际化文本资源中渲染`{}`表达式，这一步只是简单的文本替换。当所有的`{}`表达式被渲染完后，在使用[Java EL](https://jcp.org/en/jsr/detail?id=341)渲染`${}`表达式，在这一步中，表达式中变量将会被替换，并完成表达式的计算，然后将得到的计算结果渲染到文本中。

也就是说，Bean Validation对`{}`的解析要优先于`${}`，如果`{message}`可以被解析成`value`，那么`${message}`会被解析成`$value`，而不是`value`。

除了国际化文本资源外，Bean Validation还会将一些属性放入上下文中，以供渲染时使用：

1. 约束中的各个属性，按属性名添加到上下文中，可以被`{}`表达式解析；
2. 被校验的值，按`validatedValue`的名字，添加到上下文中。只会被`${}`表达式解析；
3. 一个内置的实例对象`formatter`，提供一个文本格式化方法`format(String format, Object... args)`，具体使用方法请参考`java.util.Formatter.format(String format, Object... args)`。只会被`${}`表达式解析；

#### 小例子

以`@Max`约束为例：

```java
public class AssetCreateRequest {
// ...
  @Max(450)
  private Double weight;
// ...
}
```

它的文本描述符是`{javax.validation.constraints.Size.message}`：

```java
public @interface Max {

 String message() default "{javax.validation.constraints.Max.message}";
//...more attributes
}
```
在第一轮解析中，Bean Validation在国际化资源文件中找到了对应的文本：
```properties
javax.validation.constraints.Max.message             = must be less than or equal to {value}
```

发现其中还有`{}`表达式，再进行解析，在上下文中找到了约束中的属性`value`，以此替换`{value}`，最终得到：

```
must be less than or equal to 450
```

更详细的文本渲染使用方法，请参考[官方文档](https://beanvalidation.org/2.0/spec/#validationapi-message)。

## 自定义约束文本

首先，我们需要自定义文本国际化资源文件：`violation.properties`和`violation_zh.properties`；

然后，定义一个Spring配置类，加载这些资源文件到Spring上下文中：

```java
@Configuration
public class ValidationConfig {

  @Bean
  public MessageSource messageSource() {
    ReloadableResourceBundleMessageSource messageSource
        = new ReloadableResourceBundleMessageSource();

    messageSource.setBasename("classpath:violation");
    messageSource.setDefaultEncoding("UTF-8");
    return messageSource;
  }

  @Bean
  public LocalValidatorFactoryBean validatorFactoryBean() {
    LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
    bean.setValidationMessageSource(messageSource());
    return bean;
  }

}
```

然后，重写`@ExpirationDateConstraint`的文本描述符，按照官方推荐的方式：

> 建议配置为类的全路径名加上`.message`

```java
public @interface ExpirationDateConstraint {

  String message() default "{com.github.hallwong.sessions.beanvalidator.dto.constraints.ExpirationDateConstraint.message}";
// ...
}
```

最后，在资源文件中，写入对应的文本：

`violation.properties`：

```properties
com.github.hallwong.sessions.beanvalidator.dto.constraints.ExpirationDateConstraint.message=\
  The expirationDate[${validatedValue.expirationDate}] should not be early than \
  ${!allowEqualToEffectiveDate?'or equal to ':''}the effectiveDate[${validatedValue.effectiveDate}].
```

`violation_zh.properties`：

```properties
com.github.hallwong.sessions.beanvalidator.dto.constraints.ExpirationDateConstraint.message=\
  过期时间[${validatedValue.expirationDate}]不能在生效时间[${validatedValue.effectiveDate}]\
  之前${!allowEqualToEffectiveDate?'或当天':''}。
```

## 动手练习

运行测试，更改`AssetCreateRequest`中`@ExpirationDateConstraint`约束的属性`allowEqualToEffectiveDate`值，观察测试`when_create_asset_should_return_bad_request_given_exp_date_before_eff_date`与`when_create_asset_should_return_bad_request_given_exp_date_before_eff_date_in_cn`文本的变化。

## 本节结束

好了，本节到此结束，本节内容主要是展示了Bean Validation如何通过两种不同的表达式渲染文本的。

下一节将会讲到约束中另外一个属性`groups`，请切换到`06_groups`分支继续下一节。







