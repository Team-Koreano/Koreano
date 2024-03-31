package org.ecommerce.common.advice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.ecommerce.common.vo.Response;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class LoggingAspect {

    private final HttpServletRequest request;


    @Before("execution(* org.ecommerce.*.controller..*Controller.*(..))")
    public void beforeAdvice() {
        String endpoint = request.getRequestURI(); // 현재 요청의 엔드포인트 경로를 얻어옴

        log.info("\u001B[34mAPI 호출! - "+endpoint + "\u001B[0m");
    }

    @AfterReturning(pointcut  = "execution(* org.ecommerce.*.controller.*Controller.*(..))",  returning = "returnValue")
    public void afterAdvice(Object returnValue) {
        String endpoint = request.getRequestURI(); // 현재 요청의 엔드포인트 경로를 얻어옴
        if(returnValue instanceof Response<?>){
//            log.info("\u001B[32mAPI 호출 완료! - "+endpoint + " : "+ ((Response<?>) returnValue).getMessage() +"\u001B[0m");
            return;
        }
        log.info("\u001B[32mAPI 호출 완료! - "+endpoint + "\u001B[0m");
    }

}