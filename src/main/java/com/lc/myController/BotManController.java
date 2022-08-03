package com.lc.myController;

import cn.hutool.cache.impl.WeakCache;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.corundumstudio.socketio.SocketIOServer;
import com.lc.myAspect.annotation.SysLog;
import com.lc.myEntity.BotManBody;
import com.lc.myEntity.ResultBody;
import com.lc.myEnum.CommonEnum;
import com.lc.mySocketIO.MyCache;
import com.lc.mySocketIO.SocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

/**
 * Controller 控制层
 * <p>
 * BotMan的接口 | webhook
 */
@RestController
@RequestMapping(value = "/api")
public class BotManController {

	/**
	 * socketIOServer
	 */
	@Autowired
	SocketIOServer thisSocketIOServer;

	/**
	 * SocketHandler
	 */
	@Autowired
	SocketHandler thisSocketHandler;

	/**
	 * MyCache
	 */
	@Autowired
	MyCache thisMyCache;

	/**
	 * BotMan 接口,推送数据接口
	 * <p>
	 * // produces = "application/json;charset=utf-8"
	 * // produces = MediaType.APPLICATION_JSON_VALUE
	 * -------------------请求数据模板--------------------
	 * {
	 * "userId": "ahviplc",
	 * "token": 112233,
	 * "msgType": "text",
	 * "msgContents": "我来自【http://192.168.0.16:9528/api/bm】接口"
	 * }
	 * -------------------请求数据模板--------------------
	 *
	 * @param thisBotManBody
	 * @return
	 */
	@SysLog(value = "我是BotMan接口切面日志,推送数据接口")
	@RequestMapping(value = "/bm", method = {RequestMethod.POST}, produces = "application/json;charset=utf-8")
	public ResultBody BotMan(@RequestBody(required = false) BotManBody thisBotManBody) {

		// 必填校验
		if (ObjectUtil.isNull(thisBotManBody.getUserId()) || ObjectUtil.isNull(thisBotManBody.getToken()) || ObjectUtil.isNull(thisBotManBody.getMsgContents())) {
			return ResultBody.error(CommonEnum.BODY_NOT_MATCH);
		}

		Console.log(" 请求体 BotManBody => {}", JSONUtil.toJsonStr(thisBotManBody));

		// 判断此用户是否已经在线
		// 如果为true 就是在线 直接下发下去
		Map<String, UUID> thisClientMap = thisSocketHandler.getClientMap();
		Console.log(" 判断此用户是否已经在线 getClientMap => {}", JSONUtil.toJsonStr(thisClientMap));
		Console.log(" 判断此用户是否已经在线 getClientMap size => {}", thisClientMap.size());
		Console.log(" 判断此用户是否已经在线 getClientMap containsKey => {} 为false 代表 {} 还不在线", thisClientMap.containsKey(thisBotManBody.getUserId()), thisBotManBody.getUserId());

		if (thisClientMap.size() > 0 && thisClientMap.containsKey(thisBotManBody.getUserId())) {
			// 获取此在线用户的 sessionid
			UUID thisSessionId = thisClientMap.get(thisBotManBody.getUserId());
			// 通过 thisSocketIOServer 和 此在线用户的 sessionid 获取 SocketIOClient
			// 然后进行消息的下发
			thisSocketIOServer.getClient(thisSessionId).sendEvent("botManFromServer", thisBotManBody.getUserId() + " 你有一条Msg | " + thisBotManBody.getMsgContents());
			return ResultBody.success("BotMan Push Success");
		}

		// 到这里 就是不在线 直接缓存起来 等它上线补发即可
		// Done
		WeakCache<Object, Object> weakCache = thisMyCache.createCacheManager();
		weakCache.put(thisBotManBody.getUserId() + "#" + RandomUtil.randomString(8), JSONUtil.toJsonStr(thisBotManBody));
		Console.log("此待发weakCache数据中 待发消息 => {}", JSONUtil.toJsonStr(weakCache));

		return ResultBody.success("BotMan Push Later When You are Online");
	}
}

