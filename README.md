## 欢迎使用Java Bean Validation

Java Bean Validation一开始是一个框架（应该是脱胎于Hibernate），于2009年11月16日被[JCP](https://jcp.org/en/home/index)通过审核，成为JSR成为Java EE 6的标准。如今已经推出了2.0版本，可以在Java SE中使用。

### 历史版本：

* 2.0版本： [JSR-380](https://jcp.org/en/jsr/detail?id=380)

  * 添加了对Java 8的支持（java.time包，Optional），**版本也必须是Java 8或更高**

  * 对泛型的校验：

    ```java
    List<@Positive Integer> positiveNumbers;
    ```

  * 新增了内置的约束（Constraint）:

    * `@Email`
    *  `@NotEmpty`
    *  `@NotBlank`
    *  `@Positive`
    * `@PositiveOrZero`
    * `@Negative`
    * `@NegativeOrZero`
    * `@PastOrPresent`
    * `@FutureOrPresent`

* 1.1版本： [JSR-349](https://jcp.org/en/jsr/detail?id=349)

  * 对于方法的参数和返回值的校验
  * 依赖注入的支持

* 1.0版本： [JSR-303](https://jcp.org/en/jsr/detail?id=303)

## 集成到我们的项目中

Spring Boot已经很好的集成了Bean Validation 2.0，我们只需要

1. 引入依赖；

   ```groovy
   // validation
   implementation("org.springframework.boot:spring-boot-starter-validation")
   ```

   

2. 在Resource代码上开启校验AOP；

   ```java
   import org.springframework.validation.annotation.Validated;
   
   @Validated
   // ... other annotations
   public class AssetResource {
     // ... dependency & methods
   }
   ```

   

3. 在方面参数上添加`@Valid`注解，这样这个参数就可以被Bean Validation框架接管，在方法被执行之前，对这个参数按照注解进行校验。

   ```java
   import javax.validation.Valid;
   
   // ... annotations
   public class AssetResource {
   
     // ... dependency & other methods
   
     // ... annotations
     public AssetResponse create(@Valid /*other annotations*/ AssetCreateRequest request) {
       // implements
     }
   
   }
   ```

   

这些工作我都已经在这个分支上做好了，请在开始前**记得执行一遍Gradle**，刷新依赖。

同时我还添加了一个测试文件：`AssetResourceTest`，方便我们观察程序运行的结果。

## 动手试试

首先，直接运行测试文件，可以看到响应体中的JSON是这样的。

```json
{
	"title": "Bad Request",
	"status": 400,
	"detail": "The asset key must not be null."
}
```

然后在`AssetCreateRequest`中，对属性`key`添加`NotNull`注解：

```java
import javax.validation.constraints.NotNull;

public class AssetCreateRequest {

  @NotNull
  private String key;
  
  // ... other fields
}
```

再次运行测试文件，观察运行结果发生了什么变化？

如果能在响应体中的JSON看到类似如下的内容，那么就说明Bean Validation起作用了。

```json
{
	"violations": [{
		"field": "key",
		"message": "must not be null"
	}],
  
  // ... other fileds
}
```

可以看到我们定义的异常没有抛出，说明Service中的校验方法并没有被执行，因为Bean Validation对方法参数进行了校验，发现了错误，抛出了异常，打断了方法的执行。

## 本节结束

好了，本节到此结束，本节内容主要是展示了如何集成和开启Bean Validation。下一节将会讲到将两个约束同时作用在一个属性，将会发生什么。

请切换到`03_multiple_constraints_to_one_filed`分支继续下一节。

