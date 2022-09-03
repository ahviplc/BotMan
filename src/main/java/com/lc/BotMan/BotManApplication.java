package com.lc.BotMan;

import cn.hutool.core.date.DateUtil;
import cn.hutool.log.StaticLog;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.lc.mySocketIO",
		"com.lc.myController",
		"com.lc.myBatisPlus",
		"com.lc.myException",
		"com.lc.myAspect"})
@MapperScan("com.lc.myBatisPlus.mapper")
public class BotManApplication {

	public static void main(String[] args) {
		// 隐掉初始写法
		// SpringApplication.run(MySpringBootTemplateApplication.class, args);

		// 获取context
		ConfigurableApplicationContext context = SpringApplication.run(BotManApplication.class, args);
		Environment environment = context.getBean(Environment.class);
		// 获取端口
		// System.out.println("访问链接：http://localhost:" + environment.getProperty("server.port") + environment.getProperty("server.servlet.context-path"));
		String thisPort = environment.getProperty("server.port");
		// Console.log("{},在端口{},服务程序正在运行... 首页链接 => {} | {}", DateUtil.now(), thisPort, "http://localhost:" + thisPort, "http://127.0.0.1:" + thisPort);
		// Console.log("{},在端口{},服务程序正在运行... 测试链接 => {} | {}", DateUtil.now(), thisPort, "http://localhost:" + thisPort + "/api" + "/ping", "http://127.0.0.1:" + thisPort + "/api" + "/ping");
		StaticLog.info("{},在端口{},服务程序正在运行... 首页链接 => {} | {}", DateUtil.now(), thisPort, "http://localhost:" + thisPort, "http://127.0.0.1:" + thisPort);
		StaticLog.info("{},在端口{},服务程序正在运行... 测试链接 => {} | {}", DateUtil.now(), thisPort, "http://localhost:" + thisPort + "/api" + "/ping", "http://127.0.0.1:" + thisPort + "/api" + "/ping");
	}
}
