package edu.avans.hartigehap.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class MyExecutionTimeAspect {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(MyLoggingAspect.class);

	@Pointcut("@annotation(edu.avans.hartigehap.aop.MyExecutionTime) && execution(* edu.avans.hartigehap..*(..))")
	// the pointcut expression
	public void myExecutionTimeAnnotation() { // the pointcut signature
	}

	@Around("myExecutionTimeAnnotation()")
	public Object myExecutionTimeAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
		long startMillis = System.currentTimeMillis();
		LOGGER.info("Starting timing method " + joinPoint.getSignature());
		Object retVal = joinPoint.proceed();
		long duration = System.currentTimeMillis() - startMillis;
		LOGGER.info("Call to " + joinPoint.getSignature() + " took " + duration + " ms");
		return retVal;

	}
}