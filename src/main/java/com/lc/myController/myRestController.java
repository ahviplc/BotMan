package com.lc.myController;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import cn.hutool.system.oshi.CpuInfo;
import cn.hutool.system.oshi.OshiUtil;
import com.lc.myAspect.annotation.SysLog;
import com.lc.myEntity.ResultBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import oshi.software.os.OperatingSystem;

/**
 * Controller 控制层
 * <p>
 * 一些共用的接口
 */
@RestController
@RequestMapping(value = "/api")
public class myRestController {
	/**
	 * ping pong 测试接口
	 *
	 * @return
	 */

	@SysLog(value = "我是ping接口切面日志")
	@RequestMapping(value = "/ping", method = {RequestMethod.POST, RequestMethod.GET}, produces = "application/json;charset=utf-8")
	public ResultBody ping() {
		return ResultBody.success("pong");
	}

	/**
	 * env 自定义环境变量接口
	 *
	 * @return
	 */
	@SysLog(value = "自定义环境变量接口切面日志")
	@RequestMapping(value = "/env", method = {RequestMethod.POST, RequestMethod.GET}, produces = "application/json;charset=utf-8")
	public ResultBody env() {
		String thisAppName = SystemUtil.get("APP_NAME", false);
		String thisWhoAmI = SystemUtil.get("WHO_AM_I", false);
		return ResultBody.success(Dict.create()
						.set("APP_NAME", StrUtil.isEmptyIfStr(thisAppName) ? "BotMan => Push Anything To Anywhere" : thisAppName)
						.set("WHO_AM_I", StrUtil.isEmptyIfStr(thisWhoAmI) ? "the BotMan" : thisWhoAmI)
				// .set("APP_PORT", SystemUtil.get("APP_PORT", false))
		);
	}

	/**
	 * 机器信息接口 All
	 *
	 * @return
	 */
	@SysLog(value = "全部机器信息接口切面日志")
	@RequestMapping(value = "/all", method = {RequestMethod.POST, RequestMethod.GET}, produces = "application/json;charset=utf-8")
	public ResultBody myMachineInfo() {
		CpuInfo currentCpuInfo = OshiUtil.getCpuInfo();
		OperatingSystem thisOperatingSystem = OshiUtil.getOs();
		return ResultBody.success(Dict.create()
				.set("cpu", currentCpuInfo)
				.set("cpu_desc", currentCpuInfo.toString())
				.set("mem", OshiUtil.getMemory())
				.set("disk", OshiUtil.getDiskStores())
				.set("os", thisOperatingSystem.getFamily() + StrUtil.SPACE + thisOperatingSystem.getVersionInfo().toString())
		);
	}

	/**
	 * 机器信息接口 cpu信息
	 *
	 * @return
	 */
	@SysLog(value = "cpu信息接口切面日志")
	@RequestMapping(value = "/cpu", method = {RequestMethod.POST, RequestMethod.GET}, produces = "application/json;charset=utf-8")
	public ResultBody cpu() {
		CpuInfo currentCpuInfo = OshiUtil.getCpuInfo();
		OperatingSystem thisOperatingSystem = OshiUtil.getOs();
		return ResultBody.success(Dict.create()
				.set("cpu", currentCpuInfo)
				.set("cpu_desc", currentCpuInfo.toString())
		);
	}

	/**
	 * 机器信息接口 mem 内存信息
	 *
	 * @return
	 */
	@SysLog(value = "内存信息接口切面日志")
	@RequestMapping(value = "/mem", method = {RequestMethod.POST, RequestMethod.GET}, produces = "application/json;charset=utf-8")
	public ResultBody mem() {
		return ResultBody.success(Dict.create()
				.set("mem", OshiUtil.getMemory())
		);
	}

	/**
	 * 机器信息接口 disk 硬盘信息
	 *
	 * @return
	 */
	@SysLog(value = "硬盘信息接口切面日志")
	@RequestMapping(value = "/disk", method = {RequestMethod.POST, RequestMethod.GET}, produces = "application/json;charset=utf-8")
	public ResultBody disk() {
		return ResultBody.success(Dict.create()
				.set("disk", OshiUtil.getDiskStores())
		);
	}

	/**
	 * 机器信息接口 os 系统信息
	 *
	 * @return
	 */
	@SysLog(value = "系统信息接口切面日志")
	@RequestMapping(value = "/os", method = {RequestMethod.POST, RequestMethod.GET}, produces = "application/json;charset=utf-8")
	public ResultBody os() {
		OperatingSystem thisOperatingSystem = OshiUtil.getOs();
		return ResultBody.success(Dict.create()
				.set("os", thisOperatingSystem.getFamily() + StrUtil.SPACE + thisOperatingSystem.getVersionInfo().toString())
		);
	}
}

