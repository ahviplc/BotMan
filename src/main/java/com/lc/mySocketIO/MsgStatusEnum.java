package com.lc.mySocketIO;

/**
 * 消息状态
 */
public enum MsgStatusEnum {
	/**
	 * 上线
	 */
	ONLINE("上线"),

	/**
	 * 下线
	 */
	OFFLINE("下线");

	private String value;

	MsgStatusEnum(String type) {
		value = type;
	}

	public String getValue() {
		return value;
	}
}
