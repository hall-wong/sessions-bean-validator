## 引入Java Bean Validator 2.0[^1]

在SpringBoot中，只需要在依赖中添加[`spring-boot-starter-validation`](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-validation)依赖即可：

```groovy
// validation
implementation("org.springframework.boot:spring-boot-starter-validation")
```

## 应用Java Bean Validator 2.0到代码中

首先我们将通过`@Pattern`约束（Constraint）注解，来重构对`Asset.key`的校验。

1. 在所有声明属性或参数`key`的地方添加注解：

```java
@Pattern(regexp = "(DSC-\\d{4}|OPT-\\d{5})", message = "The asset key is invalid.")
```

2. 同时将`AssetResource`类添加`@Validated`注解，这表明Spring会对这个类开启参数校验，这也是**极为重要**的一步；
3. 在想要被校验的参数前添加`@Valid`注解，这也是**极为重要**的一步；
4. 移除`AssetService`中关于`key`的校验；
5. 运行测试； 
   可以看到response中不仅仅只返回了detail，还返回了一个叫violations数组。因为在Java Bean Validator 2.0中，validator会一次性检查所有字段的约束，并记录在一个上下文中，随着异常抛出。在这个演示代码，我采用了[problem-spring-web](https://github.com/zalando/problem-spring-web)来处理一些常见的异常，它所返回的response，在格式上符合[RFC7807](https://tools.ietf.org/html/rfc7807#section-4.2)的要求。
6. 调整测试；

### 一些常见的内置校验注解

在Java Bean Validator 2.0，有着许多类型的校验注解，我将列出一些比较常见的：

* @NotNull：不得为空；
* @Size：字符串、集合或表的长度校验；
* @Digits：数字长度校验，其中`integer`代表整数部分长度，`fraction`代表小数部分长度；
* @Min：数字的最小值校验；
* @Max：数字的最大值校验；

## 小结

在本节中，我们使用了Java Bean Validator 2.0对`Asset.key`做了基于正则表达式的校验。但是如果一个参数，有着多个约束，各个约束是如何互相影响的呢？我将在`s-2`分支中继续讲解。

[^1]: [Java Bean Validator](https://beanvalidation.org/2.0/) 2.0于2019八月推出，已经成为了Java EE 8的标准。当然也通过添加依赖的方式应用到Java SE项目中。其中知名度较高的实现是由[Hibernate](https://hibernate.org/validator)提供的。
