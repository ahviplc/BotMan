package com.lc.myBatisPlus.service.impl;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lc.myBatisPlus.service.UserService;
import com.lc.myBatisPlus.mapper.UserMapper;
import com.lc.myEntity.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LC
 */
@Service
public class UserServiceImpl implements UserService {

	@Resource
	UserMapper userMapper;

	@Override
	public Integer save(User user) {
		return userMapper.insert(user);
	}

	@Override
	public Integer removeById(String id) {
		return userMapper.deleteById(id);
	}

	@Override
	public User getById(String id) {
		return userMapper.selectById(id);
	}

	@Override
	public List<User> list(Wrapper<User> queryWrapper) {
		List<User> userList = userMapper.selectList(queryWrapper);
		if (userList == null || userList.size() == 0) {
			return new ArrayList<>();
		}
		return userList;
	}

	@Override
	public IPage<User> page(Integer current, Integer size) {
		Page<User> page = new Page<>(current, size);
		return userMapper.selectPage(page, new QueryWrapper<>());
	}

	@Override
	public Integer updateById(User user) {
		return userMapper.updateById(user);
	}
}
