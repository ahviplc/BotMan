package com.lc.myAspect;

import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;
//import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

//@Slf4j
@Service
public class SysLogService {

	public boolean save(SysLogBO sysLogBO) {
		// 这里就不做具体实现了
		// log.info(sysLogBO.getParams());
		// StaticLog.info("【com.lc.myAspect.SysLogService】描述 => {}", sysLogBO.toString());
		StaticLog.info("【com.lc.myAspect.SysLogService】此Json描述 => {}", JSONUtil.toJsonStr(sysLogBO));
		return true;
	}
}
