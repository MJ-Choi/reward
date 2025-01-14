package com.product.reward.api.error;

import lombok.AllArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.Method;

/**
 * RequestBody 어노테이션 사용 시, 파라미터의 유효성 검사 응답 공통화
 */
@Aspect
@Component
@AllArgsConstructor
public class ValidationAspect {

  private final Validator validator;

  @Before("execution(* *(.., @org.springframework.web.bind.annotation.RequestBody (*), ..))")
  public void validateRequestBody(JoinPoint joinPoint) throws Throwable {
    Method method = getCurrentMethod(joinPoint);
    Object[] args = joinPoint.getArgs();
    for (int i = 0; i < args.length; i++) {
      if (method.getParameters()[i].isAnnotationPresent(RequestBody.class)) {
        Object arg = args[i];
        DataBinder binder = new DataBinder(arg);
        BindingResult result = binder.getBindingResult();
        validator.validate(arg, result);
        if (result.hasErrors()) {
          throw new ResponseException(ErrorCode.INPUT_ERROR);
        }
      }
    }
  }

  private Method getCurrentMethod(JoinPoint joinPoint) throws NoSuchMethodException {
    String methodName = joinPoint.getSignature().getName();
    Class<?>[] parameterTypes = ((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature()).getParameterTypes();
    return joinPoint.getTarget().getClass().getMethod(methodName, parameterTypes);
  }

}