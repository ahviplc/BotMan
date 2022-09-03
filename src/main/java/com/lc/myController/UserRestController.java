package com.lc.myController;

import com.lc.myAspect.annotation.SysLog;
import com.lc.myBatisPlus.service.UserService;
import com.lc.myEntity.User;
import com.lc.myException.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Controller 控制层
 * <p>
 * 用户
 */
@RestController
@RequestMapping(value = "/api")
public class UserRestController {

	// @Autowired(required = true)
	@Autowired
	UserService userService;

	@SysLog(value = "新增用户User接口切面日志")
	@PostMapping("/user")
	public boolean insert(@RequestBody User user) {
		// System.out.println("开始新增...");
		// 如果姓名为空就手动抛出一个自定义的异常！
		if (user.getName() == null) {
			throw new BizException("-1", "用户姓名不能为空！");
		}
		return true;
	}

	@SysLog(value = "更新用户User接口切面日志")
	@PutMapping("/user")
	public boolean update(@RequestBody User user) {
		// System.out.println("开始更新...");
		// 这里故意造成一个空指针的异常，并且不进行处理
		String str = null;
		str.equals("111");
		return true;
	}

	@SysLog(value = "删除用户User接口切面日志")
	@DeleteMapping("/user")
	public boolean delete(@RequestBody User user) {
		// System.out.println("开始删除...");
		// 这里故意造成一个异常，并且不进行处理
		Integer.parseInt("abc123");
		return true;
	}

	@SysLog(value = "查询用户User接口切面日志")
	@GetMapping("/user")
	public List<User> findByUser(@RequestBody User user) {
		// System.out.println("开始查询...");
		List<User> userList = new ArrayList<>();
		User user2 = new User();
		user2.setId("1");
		user2.setName("LC");
		user2.setAge(18);
		userList.add(user2);
		return userList;
	}

	@SysLog(value = "插入用户User到DB接口切面日志2")
	@PostMapping("/insert")
	public Object insert(String name) {
		User user = new User();
		user.setName(name);
		return userService.save(user);
	}

	@SysLog(value = "插入用户User到DB接口切面日志3")
	@GetMapping("/insert2")
	public int insert2() {
		User user2 = new User();
		//user2.setId("22");
		user2.setName("LC");
		user2.setAge(22);
		user2.setRegisterTime(new Date());
		user2.setEmail("admin@admin.com");
		user2.setUserName("admin");
		user2.setPassword("123456");
		return userService.save(user2);
	}

	@SysLog(value = "查询用户User接口切面日志2")
	@GetMapping("/show")
	public Object show() {
		return userService.list(null);
	}

	@SysLog(value = "删除用户User接口切面日志2")
	@DeleteMapping("/delete")
	public Object delete(String id) {
		return userService.removeById(id);
	}

	@SysLog(value = "更新用户User接口切面日志2")
	@PutMapping("update")
	public Object update(String id, String name) {
		User user = new User();
		user.setId(id);
		user.setName(name);
		return userService.updateById(user);
	}
}

