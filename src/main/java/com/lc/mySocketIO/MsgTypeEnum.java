package com.lc.mySocketIO;

/**
 * 消息类型 枚举类 MsgTypeEnum
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
	COMMENT("comment"),

	/**
	 * 实时消息
	 */
	RealTimeMessage("RealTimeMessage"),

	/**
	 * 缓存消息
	 */
	CachedMessage("CachedMessage");

	private String value;

	MsgTypeEnum(String type) {
		value = type;
	}

	public String getValue() {
		return value;
	}
}
