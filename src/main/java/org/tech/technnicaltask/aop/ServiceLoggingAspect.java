package org.tech.technnicaltask.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class ServiceLoggingAspect {

	@Pointcut("execution(public * org.tech.technnicaltask.service.TaskService.*(..))")
	public void taskServicePublicMethodPointcut(){}

	@Before("taskServicePublicMethodPointcut()")
	public void doBeforeServiceMethod(JoinPoint jp) {
		MethodSignature methodSignature = (MethodSignature) jp.getSignature();
		log.info("Method '{}' started processing with args: {}",
				methodSignature.getMethod().getName(),
				jp.getArgs());
	}

	@After("taskServicePublicMethodPointcut()")
	public void doAfterServiceMethod(JoinPoint jp) {
		MethodSignature methodSignature = (MethodSignature) jp.getSignature();
		log.info("Method '{}' finished processing", methodSignature.getMethod().getName());
	}

}
