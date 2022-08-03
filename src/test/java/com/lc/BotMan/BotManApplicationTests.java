package com.lc.BotMan;

import cn.hutool.core.lang.Console;
import cn.hutool.system.SystemUtil;
import cn.hutool.system.oshi.CpuInfo;
import cn.hutool.system.oshi.OshiUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BotManApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void test() {
		// 运行时信息，包括内存总大小、已用大小、可用大小等
		System.out.println(SystemUtil.getRuntimeInfo());
		// 系统信息
		System.out.println(SystemUtil.getOsInfo());
		// 输出CPU的一些信息
		CpuInfo cpuInfo = OshiUtil.getCpuInfo();
		Console.log(cpuInfo);
		// 输出内存
		System.out.println(OshiUtil.getMemory());
		// 获取内存总量
		// System.out.println(OshiUtil.getMemory().getTotal());
		// System.out.println(OshiUtil.getNetworkIFs());
		System.out.println(OshiUtil.getOs());
		System.out.println(OshiUtil.getOs().getFamily());
		System.out.println(OshiUtil.getOs().getVersionInfo().toString());
		// System.out.println(SystemUtil.getOsInfo());
		// System.out.println(OshiUtil.getSystem());
		System.out.println(OshiUtil.getSensors());
		// System.out.println(OshiUtil.getDiskStores());
		// System.out.println(OshiUtil.getProcessor());
		// System.out.println(OshiUtil.getCurrentProcess());

		System.out.println(SystemUtil.getUserInfo());

		// 输出环境变量属性
		System.out.println(SystemUtil.get("user.country", false));
	}
}
