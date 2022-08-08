package com.lc.mySocketIO;

/**
 * MyIOUtils 我的 socket io 工具类
 */
public class MyIOUtils {

	/**
	 * 实时消息
	 *
	 * @param msgContent
	 * @return
	 */
	public static MessageDTO RealTimeMessage(String msgContent) {
		return new MessageDTO(MsgTypeEnum.RealTimeMessage.getValue(), msgContent);
	}

	/**
	 * 缓存消息
	 *
	 * @param msgContent
	 * @return
	 */
	public static MessageDTO CachedMessage(String msgContent) {
		return new MessageDTO(MsgTypeEnum.CachedMessage.getValue(), msgContent);
	}

	/**
	 * 自定义消息 全部参数
	 *
	 * @param userId
	 * @param toUserId
	 * @param beOperatedId
	 * @param msgType
	 * @param msgContent
	 * @return
	 */
	public static MessageDTO All(String userId, String toUserId, String beOperatedId, String msgType, String msgContent) {
		return new MessageDTO(userId, toUserId, beOperatedId, msgType, msgContent);
	}
}
