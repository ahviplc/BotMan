package com.lc.mySocketIO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 消息数据传输对象 MessageDTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO implements Serializable {
	/**
	 * 登录用户 Id 代表这是 userId 谁发的
	 */
	private String userId;

	/**
	 * 接收消息用户 Id 代表这是发给 toUserId 的
	 */
	private String toUserId;

	/**
	 * 被操作对象 Id
	 */
	private String beOperatedId;

	/**
	 * 消息类型
	 */
	private String msgType;

	/**
	 * 消息内容
	 */
	private String msgContent;

	public MessageDTO(String msgContent) {
		this.msgContent = msgContent;
	}

	public MessageDTO(String msgType, String msgContent) {
		this.msgType = msgType;
		this.msgContent = msgContent;
	}
}
