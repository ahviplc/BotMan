package com.lc.myAspect.aspect;

import cn.hutool.core.date.DateUtil;
import com.lc.myAspect.SysLogBO;
import com.lc.myAspect.SysLogService;
import com.lc.myAspect.annotation.SysLog;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 系统日志切面 SysLogAspect
 */
@Aspect  // 使用@Aspect注解声明一个切面
@Component
public class SysLogAspect {

	@Autowired
	private SysLogService sysLogService;

	/**
	 * 这里我们使用注解的形式
	 * 当然，我们也可以通过切点表达式直接指定需要拦截的package,需要拦截的class 以及 method
	 * 切点表达式:   execution(...)
	 * <p>
	 * execution(public * *(..)) 任意的公共方法
	 * execution（* set*（..）） 以set开头的所有的方法
	 * execution（* com.LoggerApply.*（..））com.LoggerApply这个类里的所有的方法
	 * execution（* com.annotation.*.*（..））com.annotation包下的所有的类的所有的方法
	 * execution（* com.annotation..*.*（..））com.annotation包及子包下所有的类的所有的方法
	 * execution(* com.annotation..*.*(String,?,Long)) com.annotation包及子包下所有的类的有三个参数，第一个参数为String类型，第二个参数为任意类型，第三个参数为Long类型的方法
	 * execution(@annotation(com.lingyejun.annotation.Lingyejun))
	 */
	@Pointcut("@annotation(com.lc.myAspect.annotation.SysLog)")
	public void logPointCut() {
	}

	/**
	 * 环绕通知 @Around  ， 当然也可以使用 @Before (前置通知)  @After (后置通知)
	 *
	 * @param point
	 * @return
	 * @throws Throwable
	 */
	@Around("logPointCut()")
	public Object around(ProceedingJoinPoint point) throws Throwable {
		long beginTime = System.currentTimeMillis();
		Object result = point.proceed();
		long time = System.currentTimeMillis() - beginTime;
		try {
			saveLog(point, time);
		} catch (Exception e) {
		}
		return result;
	}

	/**
	 * 保存日志
	 */
	private void saveLog(ProceedingJoinPoint joinPoint, long time) {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		SysLogBO sysLogBO = new SysLogBO();
		sysLogBO.setExeuTime(time);
		sysLogBO.setCreateDate(DateUtil.now());
		SysLog sysLog = method.getAnnotation(SysLog.class);
		if (sysLog != null) {
			// 注解上的描述
			sysLogBO.setRemark(sysLog.value());
		}
		// 请求的 类名、方法名
		String className = joinPoint.getTarget().getClass().getName();
		String methodName = signature.getName();
		sysLogBO.setClassName(className);
		sysLogBO.setMethodName(methodName);
		// 请求的参数
		Object[] args = joinPoint.getArgs();
		try {
//			List<String> list = new ArrayList<String>();
//			for (Object o : args) {
//				list.add(JSONUtil.toJsonPrettyStr(o));
//			}
			sysLogBO.setParams(args);
		} catch (Exception e) {
		}
		// 这里只是输出一下 sysLogBO 具体信息
		sysLogService.save(sysLogBO);
	}
}
