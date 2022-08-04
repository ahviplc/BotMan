package com.lc.mySocketIO;

/**
 * MyIOUtils 我的 socket io 工具类
 */
public class MyIOUtils {

	public static MessageDTO RealTimeMessage(String msgContent) {
		return new MessageDTO(MsgTypeEnum.RealTimeMessage.getValue(), msgContent);
	}

	public static MessageDTO CachedMessage(String msgContent) {
		return new MessageDTO(MsgTypeEnum.CachedMessage.getValue(), msgContent);
	}

	public static MessageDTO All(String userId, String toUserId, String beOperatedId, String msgType, String msgContent) {
		return new MessageDTO(userId, toUserId, beOperatedId, msgType, msgContent);
	}
}
