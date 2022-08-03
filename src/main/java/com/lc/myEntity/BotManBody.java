package com.lc.myEntity;

import cn.hutool.json.JSONUtil;

import java.io.Serializable;

/**
 * 实体类
 * <p>
 * BotMan的api请求实体类
 */
public class BotManBody implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 用户编号 必填
	 */
	private String userId;

	/**
	 * 用户TOKEN 用户鉴权使用 必填
	 */
	private String token;

	/**
	 * 消息类型 非必填 支持 text 和 markdown 默认 text
	 */
	private String msgType;
	/**
	 * 消息内容 必填
	 */
	private String msgContents;

	public BotManBody() {
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getMsgContents() {
		return msgContents;
	}

	public void setMsgContents(String msgContents) {
		this.msgContents = msgContents;
	}

	public String toString() {
		return JSONUtil.toJsonPrettyStr(this);
	}
}
