package com.lc.BotMan;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import cn.hutool.system.oshi.CpuInfo;
import cn.hutool.system.oshi.OshiUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lc.myBatisPlus.mapper.UserMapper;
import com.lc.myEntity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest
class BotManApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	UserMapper userMapper;

	@Test
	void test() {
		// 运行时信息，包括内存总大小、已用大小、可用大小等
		System.out.println(SystemUtil.getRuntimeInfo());
		// 系统信息
		System.out.println(SystemUtil.getOsInfo());
		// 输出CPU的一些信息
		CpuInfo cpuInfo = OshiUtil.getCpuInfo();
		Console.log(cpuInfo);
		// 输出内存
		System.out.println(OshiUtil.getMemory());
		// 获取内存总量
		// System.out.println(OshiUtil.getMemory().getTotal());
		// System.out.println(OshiUtil.getNetworkIFs());
		System.out.println(OshiUtil.getOs());
		System.out.println(OshiUtil.getOs().getFamily());
		System.out.println(OshiUtil.getOs().getVersionInfo().toString());
		// System.out.println(SystemUtil.getOsInfo());
		// System.out.println(OshiUtil.getSystem());
		System.out.println(OshiUtil.getSensors());
		// System.out.println(OshiUtil.getDiskStores());
		// System.out.println(OshiUtil.getProcessor());
		// System.out.println(OshiUtil.getCurrentProcess());

		System.out.println(SystemUtil.getUserInfo());

		// 输出环境变量属性
		System.out.println(SystemUtil.get("user.country", false));
	}

	@Test
	void testMyBatisPlusForSqliteAndMySQL() {
		// ====================================================
		// 查询所有
		// 不指定逻辑删除 默认 del_flag=0
		List<Map<String, Object>> mapList = userMapper.selectMaps(null);
		Console.log("1 => {}", mapList);
		// ====================================================

		// ====================================================
		// 条件查询
		// 构建一个查询的wrapper
		String username = null;
		// String username = "ahviplc";
		QueryWrapper<User> wrapper = new QueryWrapper<User>();
		// username不为空时，组装模糊查询条件
		// 用户名
		wrapper.like(StrUtil.isNotBlank(username), "user_name", username);

		// String name = null;
		String name = "LC";
		// name不为空时，组装模糊查询条件
		// 名称
		wrapper.like(StrUtil.isNotBlank(name), "name", name);

		// 年龄
		// wrapper.eq("age", 22);
		// 创建时间降序
		// wrapper.orderByDesc("register_time");
		// 创建时间升序
		wrapper.orderByAsc("register_time");
		// 删除标识符 未删除为0 删除为1
		wrapper.eq("del_flag", 0);

		List<User> userList = userMapper.selectList(wrapper);
		Console.log("2 => {}", userList);
		// ====================================================

		// ====================================================
		// 条件查询 | 查询已删除的
		// 构建一个查询的wrapper
		QueryWrapper<User> wrapper2 = new QueryWrapper<User>();
		// 创建时间降序
		// wrapper.orderByDesc("register_time");
		// 创建时间升序
		wrapper2.orderByAsc("register_time");
		// 删除标识符 未删除为0 删除为1
		// 这种写法是不可以的
		// 官方也不推荐查询已删除的 真正的需要频繁查询的话 不应该将其置为逻辑删除
		// 逻辑删除在设计上就是“删除”，这样设计的本意是为了防止误删，如果对“删除”的数据还有其他业务操作，那我建议使用其他字段来代替删除，
		// 这些被打上“删除”或许在后期会因数据庞大而真删
		wrapper2.eq("del_flag", 1);
		// 获取格式化之后的SQL
		//【(del_flag = ?) ORDER BY register_time ASC】
		System.out.println(wrapper2.getTargetSql());

		List<User> userList2 = userMapper.selectList(wrapper2);
		Console.log("3 => {}", userList2);
		// ====================================================

		// ====================================================
		// 查询具体的一个 查询不到 是 null
		QueryWrapper<User> wrapper3 = new QueryWrapper<User>();
		wrapper3.eq("user_name", "ahviplc");
		User userFromDB = userMapper.selectOne(wrapper3);
		Console.log("4 => {} 注册时间 {}", userFromDB, DateUtil.format(userFromDB.getRegisterTime(), DatePattern.NORM_DATETIME_FORMAT));
		// ====================================================

		// ====================================================
		// 高级条件查询
		// 查询名称是L开头的并且邮箱不为空或者是年龄大于16的用户
		// eq 等于 | ne 不等于 | gt 大于 | ge 大于等于 >= | lt 小于 | le 小于等于 |
		// 构建一个查询的wrapper
		// 注意 空白字符串 和 空 null 不等价
		QueryWrapper<User> wrapper4 = new QueryWrapper<User>();
		// and方法嵌套
		wrapper4.likeRight("name", "L").and(
				userQueryWrapper -> userQueryWrapper.isNotNull("email")
						.or().gt("age", 16)
		);
		// 删除标识符 未删除为0 删除为1
		wrapper4.eq("del_flag", 0);
		// 创建时间降序
		wrapper4.orderByDesc("register_time");
		List<User> userList3 = userMapper.selectList(wrapper4);
		Console.log("5 => {}", userList3);
		// ====================================================

		// ====================================================
		// 删除
		// Console.log("6 => {}", userMapper.deleteById("1566594703233679363"););
		// ====================================================
	}
}
