package com.lc.mySocketIO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 消息数据传输对象
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO implements Serializable {
	/**
	 * 登录用户 Id
	 */
	private String userId;

	/**
	 * 接收消息用户 Id
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
}
