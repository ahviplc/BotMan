package com.lc.mySocketIO;

/**
 * 消息类型
 */
public enum MsgTypeEnum {
	/**
	 * 关注
	 */
	FOLLOW("follow"),

	/**
	 * 认同
	 */
	LIKE("like"),

	/**
	 * 评论
	 */
	COMMENT("comment");

	private String value;

	MsgTypeEnum(String type) {
		value = type;
	}

	public String getValue() {
		return value;
	}
}
