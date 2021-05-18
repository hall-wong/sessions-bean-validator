## Java SE环境下的Bean Validation

虽然Spring已经在事实上成为了Java开发的标准库，但在有些情况下，我们仍会有可能会在Java SE下开发程序。本节将指导大家如何在Java SE环境下应用Bean Validation。

## 依赖包

没有了Spring依赖管理器，我们需要手动维护项目中的依赖。

Bean Validation至少需要两个依赖：

1. Bean Validation的实现：本例中的`("org.hibernate.validator:hibernate-validator:6.1.7.Final"`
2. Java EL的实现，用于文本的渲染：本例中的`"org.glassfish:javax.el:3.0.0"`

对于Bean Validation API的依赖，是可选的，因为在Bean Validation的实现中，都会声明其依赖。出于对依赖的稳定的考虑（如果我们更改了实现的版本，有可能对应的API版本会发生变化，而这可能会引发一些异常），我们一般会显示声明其版本：本例中的`"javax.validation:validation-api:2.0.1.Final"`。

```groovy
dependencies {
// ...
    // validation
    implementation("javax.validation:validation-api:2.0.1.Final")
    implementation("org.hibernate.validator:hibernate-validator:6.1.7.Final")
    implementation("org.glassfish:javax.el:3.0.0")
// ...
}
```

## 获取Validator的实例

在Bean Validation中，`Validator`实例是通过`ValidatorFactory`获得的。对于无定制的`ValidatorFactory`，可以直接调用`Validation#buildDefaultValidatorFactory`方法：

```java
public class ValidationUtils{
// ...
  private void init(){
    ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
  }
// ...
}

```

如果出现了如下异常，请检查`classpath`中是否有存在Bean Validation的实现。

```diff
javax.validation.NoProviderFoundException: Unable to create a Configuration, because no Bean Validation provider could be found. Add a provider like Hibernate Validator (RI) to your classpath.
```

对于目前的代码，我们还需要引入`violation`文本国际化资源包，所以我们需要对`ValidatorFactory`进行一定的定制：


```java
public class ValidationUtils{
// ...
  private void init(){
    ResourceBundleMessageInterpolator violationMessageInterpolator = new
      ResourceBundleMessageInterpolator(new PlatformResourceBundleLocator("violation"));
    ValidatorFactory validatorFactory = Validation.byDefaultProvider().configure()
      .messageInterpolator(violationMessageInterpolator)
      .buildValidatorFactory();
  }
// ...
```

**注意**：

1. ValidatorFactory继承自`AutoCloseable`，请在释放资源时记得将其关闭；
2. Hibernate中的`PlatformResourceBundleLocator`，在底层实际上是调用了JDK中的`PropertyResourceBundle`，而`PropertyResourceBundle`在读取文本时，默认使用`ISO 8859-1`，这就会导致程序在处理汉语等字符时，出现乱码。遇到这中情况，有两种处理方法：
   1. 将这些乱码字符转化成ASCII unicode字符，可以通过JDK提供的命令行工具`native2ascii`来完成转换；
   2. 提供一个MessageInterpolator实现类，在读取文本时，以UTF8字符集读取文本，具体可以参照Spring中的`HibernateValidatorDelegate#buildMessageInterpolator(MessageSource)`这个方法；

## Validator的API

首先，我们回顾下注解`@Valid`可以出现的位置：

```java
@Target({ METHOD, FIELD, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Documented
public @interface Valid {
}
```

分别是：

1. `METHOD`：方法上，检查方法的返回值是否满足声明的约束；
2. `FILED`：属性上，级联检查该属性是否满足声明的约束；
3. `CONSTRUCTOR`：构造器上，与`METHOD`类似，检查返回的实例是否满足声明的约束；
4. `PARAMETER`：参数上，检查方法或构造器中的被注解参数是否满足声明的约束；
5. `TYPE_USE`：这个类型稍微特殊一些，是1.8之后新加入的，几乎可以注解在任何一个位置上，Bean Validation在2.0中将其填入，主要是满足`List<@Valid Integer> list`这样的约束校验。本节末也会附上一种特殊的使用情况；

### 动手练习

在测试类`ValidationTest`中，我加入了几个测试，展示如何调用`Validator`中的API，来满足对不同位置`@Valid`的校验，以及一个`fail_fast_mode_example`来展示之前提到的`Fail Fast Mode`（即只展示第一个约束违例）。

## 本节结束

好了，本节到此结束，本节内容主要是展示了在Java SE环境下搭建Bean Validation，并调用相关接口校验方法或实例对象。

本教程到这里已经基本结束了，在教程中，详细介绍了Bean Validation常用的注解、方法、类和接口，大家可以自行对比下`07_payload`与`01_a_better_case`这两个分支上的代码，特别是`AssetCreateRequest`和`AssetService`这两个类，有什么样子的变化。

最后，感谢阅读到这里，希望大家可以在实际工作中使用到Bean Validation这个框架。

## 附：当`@Valid`出现了在方法内的参数上

感谢我的一位小伙伴，他提供了一段他见过的代码，其中`@Valid`注解放在一个方法内参数上：

```java
public class Foo {
  public void boo() {
    @Valid @NotBlank String str = "";
    // ...
  }
}
```

大家可以思考下，Bean Validation会对这个`@NotBlank`约束执行校验吗？

我们可以试着回忆下，如果我们想要拿到一个注解的实例，是通过`AnnotatedElement#getAnnotation(Class)`这个方法吧？那么就要首先获得`AnnotatedElement`的一个实现类的实例，`AnnotatedElement`的常见实现类有`Method`、`Class`、`Parameter`等，这些也是存放在JVM方法区中的数据。但是方法内的参数实际上是没有保存在JVM方法区的，所以我们**无法**在程序运行时，动态地通过反射获取到一个方法内参数上的注解。因此Bean Validation无法校验出这个`NotBlank`约束是否满足。

那么是否意味着这么写就完全无用了呢？

根据JDK的[官方文档](https://docs.oracle.com/javase/tutorial/java/annotations/type_annotations.html)描述：

> Type annotations were created to support improved analysis of Java programs way of ensuring stronger type checking. The Java SE 8 release does not provide a type checking framework, but it allows you to write (or download) a type checking framework that is implemented as one or more pluggable modules that are used in conjunction with the Java compiler.

这里解释了注解的另一个用处：方便代码检查工具对代码进行静态校验。所以这里的`@Valid`注解，很有可能是为了方便代码检查工具校验这里是否可能会有潜在的空字符串。我试着去搜索有没有可以支持`@Valid`注解的静态代码检查工具，但是目前没有发现。

