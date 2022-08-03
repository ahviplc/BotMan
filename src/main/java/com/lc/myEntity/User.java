package com.lc.myEntity;

import cn.hutool.json.JSONUtil;

import java.io.Serializable;

/**
 * 实体类
 * <p>
 * 万能的用户表
 */
public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 编号
	 */
	private String id;
	/**
	 * 姓名
	 */
	private String name;
	/**
	 * 年龄
	 */
	private int age;

	public User() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String toString() {
		return JSONUtil.toJsonPrettyStr(this);
	}
}
