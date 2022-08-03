package com.lc.myHandler;

import cn.hutool.log.StaticLog;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 此对捕获的异常进行简单的二次处理，返回异常的信息，虽然这种能够让我们知道异常的原因，但是在很多的情况下来说，
 * 可能还是不够人性化，不符合我们的要求。那么我们这里可以通过自定义的异常类以及枚举类来实现我们想要的那种数据吧
 * 详细见 com/lc/myException/GlobalExceptionHandler.java
 * <p>
 * 如何启用这个呢？
 * 在【com.lc.BotMan.MySpringBootTemplateApplication】上加上注解【@ComponentScan(value = "com.lc.myHandler")】即可启用
 * 还有与此同时 要去掉更高级更人性化的异常处理的注解【@ComponentScan(value = "com.lc.myException")】
 * 即可起作用
 * <p>
 * 假如两者同时存在
 * 【@ComponentScan(value = "com.lc.myException")】
 * 【@ComponentScan(value = "com.lc.myHandler")】
 * 谁的注解在上面 谁起作用
 * <p>
 * 但是推荐使用更为优雅的处理【com.lc.myException.GlobalExceptionHandler】
 * 也就是注解【@ComponentScan(value = "com.lc.myHandler")】
 * <p>
 * 这样添加此注解的原因 请看
 * 【README.md】注意点
 */
@ControllerAdvice
public class MyExceptionHandler {

	@ExceptionHandler(value = Exception.class)
	public String exceptionHandler(Exception e) {
		// System.out.println("未知异常！原因是:" + e);
		StaticLog.info("【com.lc.myHandler.MyExceptionHandler】未知异常！原因是:{}", e);
		return e.getMessage();
	}
}
