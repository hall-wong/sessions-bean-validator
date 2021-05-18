package com.github.hallwong.sessions.beanvalidator.conf;

import com.github.hallwong.sessions.beanvalidator.validation.CustomMethodValidationPostProcessor;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Validator;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.validation.beanvalidation.FilteredMethodValidationPostProcessor;
import org.springframework.boot.validation.beanvalidation.MethodValidationExcludeFilter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.MethodParameter;
import org.springframework.core.env.Environment;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.ControllerAdviceBean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@Configuration
public class ValidationConfig implements InitializingBean {

  @Autowired
  private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

  @Autowired
  private ApplicationContext ac;

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

  @Bean
  public MethodValidationPostProcessor customMethodValidationPostProcessor(
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

  // 这部分代码主要是跳过RequestResponseBodyMethodProcessor中的校验逻辑，统一交给Bean Validation框架做处理。
  // 因为当前的实现没有办法很好的扩展，暂时写成这样。
  // 部分摘自Spring源码。
  @Override
  public void afterPropertiesSet() {
    List<HandlerMethodArgumentResolver> unmodifiableList = requestMappingHandlerAdapter
        .getArgumentResolvers();
    if (unmodifiableList == null) {
      return;
    }
    List<HandlerMethodArgumentResolver> replacedResolvers = unmodifiableList.stream()
        .map(this::replaceRequestResponseBodyMethodProcessor)
        .collect(Collectors.toList());
    requestMappingHandlerAdapter.setArgumentResolvers(replacedResolvers);
  }

  private List<Object> getAdviceBeans() {
    List<ControllerAdviceBean> adviceBeans = ControllerAdviceBean.findAnnotatedBeans(ac);

    List<Object> requestResponseBodyAdviceBeans = new ArrayList<>();

    for (ControllerAdviceBean adviceBean : adviceBeans) {
      Class<?> beanType = adviceBean.getBeanType();
      if (beanType == null) {
        throw new IllegalStateException(
            "Unresolvable type for ControllerAdviceBean: " + adviceBean);
      }
      if (RequestBodyAdvice.class.isAssignableFrom(beanType) || ResponseBodyAdvice.class
          .isAssignableFrom(beanType)) {
        requestResponseBodyAdviceBeans.add(adviceBean);
      }
    }
    return requestResponseBodyAdviceBeans;
  }

  private HandlerMethodArgumentResolver replaceRequestResponseBodyMethodProcessor(
      HandlerMethodArgumentResolver resolver) {
    if (resolver instanceof RequestResponseBodyMethodProcessor) {
      return new NoValidationRequestResponseBodyMethodProcessor(resolver);
    }
    return resolver;
  }

  private class NoValidationRequestResponseBodyMethodProcessor extends
      RequestResponseBodyMethodProcessor {

    public NoValidationRequestResponseBodyMethodProcessor(HandlerMethodArgumentResolver ss) {
      super(requestMappingHandlerAdapter.getMessageConverters(), getAdviceBeans());
    }

    @Override
    protected void validateIfApplicable(WebDataBinder binder, MethodParameter parameter) {
//      super.validateIfApplicable(binder, parameter);
    }

  }

}