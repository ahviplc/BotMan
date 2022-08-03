package com.lc.myAspect;


import lombok.Data;

/**
 * 系统日志 BO
 * <p>
 * BO（Business Object）业务对象
 */
@Data
public class SysLogBO {

	/**
	 * 类名
	 */
	private String className;

	/**
	 * 方法名
	 */
	private String methodName;

	/**
	 * 请求参数
	 */
	private Object params;

	/**
	 * 执行时间 单位 毫秒
	 */
	private Long exeuTime;

	/**
	 * 描述
	 */
	private String remark;

	/**
	 * 请求时间
	 */
	private String createDate;
}
