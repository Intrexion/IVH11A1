package edu.avans.hartigehap.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class MyLoggingAspect {
	@Pointcut("execution(* edu.avans.hartigehap..*(..))") // the pointcut expression
	public void anyHartigeHapMethod() { // the pointcut signature
	}

	@Before("anyHartigeHapMethod()")
	public void loggingBeforeAdvice(JoinPoint joinPoint) {
			System.out.println("(AOP-myLogger) Executing: "
					+ joinPoint.getSignature().getDeclaringTypeName() + "."
					+ joinPoint.getSignature().getName());
	}

	@Around("anyHartigeHapMethod()")
	public Object loggingAroundAdvice(ProceedingJoinPoint pjp)
			throws Throwable {
		System.out.println("(AOP-myLogger) Before execution: "
				+ pjp.getSignature().getDeclaringTypeName() + "."
				+ pjp.getSignature().getName());
		Object retVal = pjp.proceed();
		System.out.println("(AOP-myLogger) After execution: "
				+ pjp.getSignature().getDeclaringTypeName() + "."
				+ pjp.getSignature().getName());
		return retVal;
	}
}