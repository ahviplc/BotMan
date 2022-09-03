package com.lc.myBatisPlus.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lc.myEntity.User;

import java.util.List;

/**
 * @author LC
 */
public interface UserService {

	/**
	 * 增加一条用户记录
	 * @param user 用户信息
	 * @return 成功标志，新增成功返回值为 1
	 */
	Integer save(User user);

	/**
	 * 通过 id 删除一条用户记录
	 * @param id 用户 id
	 * @return 成功标志，新增成功返回值为 1
	 */
	Integer removeById(String id);

	/**
	 * 通过 id 查询一条用户记录
	 * @param id 用户 id
	 * @return User 用户信息
	 */
	User getById(String id);

	/**
	 * 通过 条件 查询符合的用户记录
	 * @param queryWrapper 查询条件
	 * @return List<User> 用户列表
	 */
	List<User> list(Wrapper<User> queryWrapper);

	/**
	 * 分页查询用户记录
	 * @param current 当前页数
	 * @param size 每页展示数目
	 * @return IPage<User> 用户列表
	 */
	IPage<User> page(Integer current, Integer size);

	/**
	 * 通过 id 修改一条用户记录
	 * @param user 用户信息
	 * @return 成功标志，新增成功返回值为 1
	 */
	Integer updateById(User user);
}