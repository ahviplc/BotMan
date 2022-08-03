package com.lc.myInterface;

/**
 * 自定义基础接口类
 * <p>
 * 首先定义一个基础的接口类，自定义的错误描述枚举类需实现该接口
 */
public interface BaseErrorInfoInterface {
	/**
	 * 错误码
	 */
	String getResultCode();

	/**
	 * 错误描述
	 */
	String getResultMsg();
}

