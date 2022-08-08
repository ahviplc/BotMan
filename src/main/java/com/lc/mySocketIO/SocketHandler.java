package com.lc.mySocketIO;

import cn.hutool.cache.impl.FIFOCache;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Socket 处理器 SocketHandler 类
 *
 * @Component加到类路径自动扫描.
 * @component （把普通pojo实例化到spring容器中，相当于配置文件中的 <bean id="" class=""/>）
 * 泛指各种组件，就是说当我们的类不属于各种归类的时候（不属于@Controller、@Services等的时候），我们就可以使用@Component来标注这个类。
 * --------------------------------------------------------------
 * @Autowired表示一个属性是否需要进行依赖注入，可以使用在属性、普通方法上、构造方法上。注解中的required属性默认是true，如果没有对象可以注入到属性，则会报出异常；
 * @Autowired使用在构造方法中：根据构造方法的形参、形参名，从ioc容器中找到该类型的Bean对象，注入到构造方法的形参中，并且执行该方法；
 */
@Component
public class SocketHandler {

	/**
	 * ConcurrentHashMap 保存当前 SocketServer 用户 Id 和其当前用户有效socket连接的sessionId 对应关系
	 */
	private Map<String, UUID> clientMap = new ConcurrentHashMap<>(16);

	/**
	 * socketIOServer
	 */
	private final SocketIOServer socketIOServer;

	@Autowired
	public SocketHandler(SocketIOServer server) {

		// it can work 这里我设置 半分钟 30秒 客户端ping一下 设置时间越短 ping出错的概率就大
		// 配置点在【application.properties:43】
		// SocketIOServer 新增 ping 的监听器
		// lambda写法
		server.addPingListener(client -> {
			StaticLog.info("----------------------------------------------------------------------------------------------------");
			StaticLog.info("Ping来了 getSessionId => {}", client.getSessionId());
			StaticLog.info("Ping来了 getUrlParams 握手数据中的参数 => {}", client.getHandshakeData().getUrlParams());
			StaticLog.info("----------------------------------------------------------------------------------------------------");
		});

// 常规写法
//		server.addPingListener(new PingListener() {
//			@Override
//			public void onPing(SocketIOClient client) {
//				StaticLog.info("----------------------------------------------------------------------------------------------------");
//				StaticLog.info("Ping来了 getSessionId => {}", client.getSessionId());
//				StaticLog.info("Ping来了 getUrlParams 握手数据中的参数 => {}", client.getHandshakeData().getUrlParams());
//				StaticLog.info("----------------------------------------------------------------------------------------------------");
//			}
//		});

		this.socketIOServer = server;
	}

	public Map<String, UUID> getClientMap() {
		return this.clientMap;
	}

	@Autowired
	MyCache thisMyCache;

	/**
	 * 当客户端发起连接时调用
	 *
	 * @param socketClient socketClient
	 */
	@OnConnect
	public void onConnect(SocketIOClient socketClient) {

		StaticLog.info("----------------------------------------------------------------------------------------------------");

		String userId = socketClient.getHandshakeData().getSingleUrlParam("userId");

		// 可用 准确
		// System.out.println(getIpByClient(socketClient));

		// 可以判断用户名是否已经上线 已经上线的 缓存中此用户名已经存在 将此用户的新的 sessionId 建立的 socketClient 断开(断开之前 通过事件告知一下其此用户名已上线)
		if (clientMap.containsKey(userId)) { // 已经上过线
			// 定向发送消息
			// socketIOServer.getClient(socketClient.getSessionId()).sendEvent("botManFromServer", " 定向通知消息 => " + userId + " 已经上线过了 请换个用户名再试连接 当前Connect的sessionId为 => " + socketClient.getSessionId() + " 当前已被服务端disconnect断开...");
			sendMsgOnEvent(socketClient.getSessionId(), "botManFromServer", " 定向通知消息 => " + userId + " 已经上线过了 请换个用户名再试连接 当前Connect的sessionId为 => " + socketClient.getSessionId() + " 当前已被服务端disconnect断开...");

			// 这个有广播的效果 群发消息
			// socketIOServer.getBroadcastOperations().sendEvent("botManFromServer", " 广播通知消息 => " + userId + " 此用户名被尝试二次Connect上线 当前Connect拒绝执行 此sessionId为 => " + socketClient.getSessionId() + " 当前已被服务端disconnect断开...");
			sendMsgOnEventByBroadcastOperations("botManFromServer", " 广播通知消息 => " + userId + " 此用户名被尝试二次Connect上线 当前Connect拒绝执行 此sessionId为 => " + socketClient.getSessionId() + " 当前已被服务端disconnect断开...");

			StaticLog.info(" 广播通知消息 => {} 此用户名被尝试二次Connect上线 当前Connect拒绝执行 此sessionId为 => {} 当前已被服务端disconnect断开...", userId, socketClient.getSessionId());
			StaticLog.info("----------------------------------------------------------------------------------------------------");
			// 服务端 对此【socketClient.getSessionId()】执行断开连接
			socketClient.disconnect();
			return;
		}
		// 如果此用户之前没有上线过的话 直接将此用户的 sessionId 存入 上线缓存 clientMap
		// 接着下面逻辑
		if (StrUtil.isNotBlank(userId)) {
			// TODO 这里其实就是需要查库或者什么操作 确定用户的真实性 目前这里不做处理
			// userService.queryUserById(userId) != null
			if (true) {
				// System.out.println("用户{" + userId + "}开启长连接通知, NettySocketSessionId: {" + socketClient.getSessionId().toString() + "},NettySocketRemoteAddress: {" + socketClient.getRemoteAddress().toString() + "}");
				StaticLog.info("用户 {} 开启长连接通知, NettySocketSessionId: {}, NettySocketRemoteAddress: {}", userId, socketClient.getSessionId().toString(), socketClient.getRemoteAddress().toString());
				// 保存
				clientMap.put(userId, socketClient.getSessionId());
				// Console.log("用户 {} 上线了 保存到 clientMap 成功 最新数据 => {}",userId, JSONUtil.toJsonStr(clientMap));
				StaticLog.info("用户 {} 上线了 保存到 clientMap 成功 最新数据 => {}", userId, JSONUtil.toJsonStr(clientMap));
				// 发送上线通知
				// this.sendNotice(new MessageDTO(userId, null, null, MsgStatusEnum.ONLINE.getValue()));
				this.sendNotice(userId + " 上线了 ... ");
				// 广播消息
				// socketIOServer.getBroadcastOperations().sendEvent("botManFromServer", "广播消息 " + userId + " 你真的上来了");
				sendMsgOnEventByBroadcastOperations("botManFromServer", "广播消息 " + userId + " 你真的上来了");

				// 单独发 测试
				// socketIOServer.getClient(socketClient.getSessionId()).sendEvent("botManFromServer", userId + " 你真的上来了");
				sendMsgOnEvent(socketClient.getSessionId(), "botManFromServer", userId + " 你真的上来了");

				// socketIOServer.getClient(socketClient.getSessionId()).sendEvent("message", userId + " 你真的上来了2");
				sendMsgOnEvent(socketClient.getSessionId(), "message", userId + " 你真的上来了2");
			}

			// 看看有没有缓存的待发
			// 创建弱引用缓存
			// WeakCache<Object, Object> weakCache = thisMyCache.createWeakCacheManager();
			// 创建FIFO(first in first out) 先进先出缓存
			FIFOCache<Object, Object> weakCache = thisMyCache.createFIFOCacheManager();
			if (weakCache.size() == 0) {
				// Console.log("===此待发weakCache数据中 无待发消息===");
				StaticLog.info("===此待发weakCache数据中 无待发消息===");
				StaticLog.info("----------------------------------------------------------------------------------------------------");
				return;
			}
			// 走到这里 代表有待发
			// Console.log("===此待发weakCache数据中 {}条待发消息 开始逐步下发 开始 => {}", weakCache.size(), JSONUtil.toJsonStr(weakCache));
			StaticLog.info("===此待发weakCache数据中 {} 条待发消息 开始逐步下发 开始 => {}", weakCache.size(), JSONUtil.toJsonStr(weakCache));
			// 遍历所有的 keys
			for (Object thisKey : weakCache.keySet()) {
				String[] strArray = thisKey.toString().split("#");
				String userIdFromCache = strArray[0];
				// 发送 userId 的 待发
				if (userIdFromCache.equalsIgnoreCase(userId)) {
					UUID thisSessionId = clientMap.get(userIdFromCache);
					// Console.log("此待发weakCache数据中 缓存待发 thisSessionId => {}", thisSessionId);
					JSONObject jsonObject = JSONUtil.parseObj(weakCache.get(thisKey));
					// 从 jsonObject 中拿出待发内容 下发下去
					// 从前端socket事件 onEventMsgSendStatusToHTML 下发 | 结构体消息数据
					// socketIOServer.getClient(thisSessionId).sendEvent("onEventMsgSendStatusToHTML", MyIOUtils.All(userId, "", thisKey.toString(), MsgTypeEnum.CachedMessage.getValue(), userIdFromCache + " 【cache】 " + jsonObject.get("msgContents").toString()));
					sendMsgOnEvent(thisSessionId, "onEventMsgSendStatusToHTML", MyIOUtils.All(userId, "", thisKey.toString(), MsgTypeEnum.CachedMessage.getValue(), userIdFromCache + " 【cache】 " + jsonObject.get("msgContents").toString()));

					// 将下发成功的 thisKey 从缓存中移除 这部分逻辑放在了 下面的 socket事件 onEventMsgSendStatus 中了
				}
				// 每次睡眠 500 毫秒
				ThreadUtil.safeSleep(500);
			}
			// Console.log("此待发weakCache数据中 {}条待发消息 开始逐步下发 完毕 => {}", weakCache.size(), JSONUtil.toJsonStr(weakCache));
			StaticLog.info("===此待发weakCache数据中 {} 条待发消息 开始逐步下发 完毕 => {}", weakCache.size(), JSONUtil.toJsonStr(weakCache));
		}

		StaticLog.info("----------------------------------------------------------------------------------------------------");

	}

	/**
	 * 客户端断开连接时调用，刷新客户端信息
	 *
	 * @param socketClient socketClient
	 */
	@OnDisconnect
	public void onDisConnect(SocketIOClient socketClient) {

		StaticLog.info("----------------------------------------------------------------------------------------------------");

		String userId = socketClient.getHandshakeData().getSingleUrlParam("userId");
		if (StrUtil.isNotBlank(userId)) {
			// System.out.println("用户{" + userId + "}断开长连接通知, NettySocketSessionId: {" + socketClient.getSessionId().toString() + "},NettySocketRemoteAddress: {" + socketClient.getRemoteAddress().toString() + "}");
			StaticLog.info("用户 {} 断开长连接通知, NettySocketSessionId: {}, NettySocketRemoteAddress: {}", userId, socketClient.getSessionId().toString(), socketClient.getRemoteAddress().toString());

			// 发送下线通知
			// this.sendNotice(new MessageDTO(userId, null, null, MsgStatusEnum.OFFLINE.getValue()));

			UUID thisSessionId = clientMap.get(userId);
			// Console.log("用户 {} 尝试下线了 此活跃的已上线 thisSessionId => {}", thisSessionId);
			StaticLog.info("用户 {} 尝试下线了 此活跃的已上线 thisSessionId => {}", userId, thisSessionId);

			SocketIOClient clientIsHave = socketIOServer.getClient(thisSessionId);
			// 如果 当前用户名 userId 对应的 thisSessionId 的 SocketIOClient 真的为null 那代表此用户真的没有在线的客户端了
			if (clientIsHave == null) {
				// Console.log("用户 {} 下线了 此要移除的 thisSessionId {} 对应的 SocketIOClient 已经 disconnect ...", userId, thisSessionId);
				StaticLog.info("用户 {} 下线了 此要移除的 thisSessionId {} 对应的 SocketIOClient 已经 disconnect ...", userId, thisSessionId);
				// 移除
				clientMap.remove(userId);
				// Console.log("用户 {} 下线了 此要移除的 thisSessionId {} 在clientMap移除成功 最新数据 => {}", userId, thisSessionId, JSONUtil.toJsonStr(clientMap));
				StaticLog.info("用户 {} 下线了 此要移除的 thisSessionId {} 在clientMap移除成功 最新数据 => {}", userId, thisSessionId, JSONUtil.toJsonStr(clientMap));
				// 群发 此用户的下线通知
				// 这个有广播的效果 群发消息
				// socketIOServer.getBroadcastOperations().sendEvent("botManFromServer", " 广播通知消息 => " + userId + " 此用户下线了");
				sendMsgOnEventByBroadcastOperations("botManFromServer", " 广播通知消息 => " + userId + " 此用户下线了");
			} else {
				StaticLog.info("用户 {} 断开长连接通知, 还有活跃的已上线的SocketSessionId: {}, 尝试二次同用户名上线的此SocketSessionId: {} 此时已被真正的disconnect了 ", userId, thisSessionId, socketClient.getSessionId().toString());
			}

			// 此时 SocketIOClient 为空 就不可以继续下发 sendEvent 了 不可发送消息了
			// socketIOServer.getClient(thisSessionId).sendEvent("botManFromServer", "你真的走了");
			// sendMsgOnEvent(thisSessionId, "botManFromServer", "你真的走了");
			// socketIOServer.getClient(thisSessionId).sendEvent("message", "你真的走了");
			// sendMsgOnEvent(thisSessionId, "message", "你真的走了");

		}

		StaticLog.info("----------------------------------------------------------------------------------------------------");
	}

	/**
	 * TODO 这个目前未启用
	 * 前端往后端 receiveMsg2 发的话 简单的string字符串 不可逆向为 MessageDTO 会报错
	 * 后端向前端 通过 receiveMsg 发送事件 发送消息
	 * <p>
	 * receiveMsg2：   接收前端消息,方法名需与前端一致
	 * receiveMsg：前端接收后端发送数据的方法，方法名需与前端一致
	 *
	 * @param socketClient socketClient
	 * @param messageDTO   messageDTO
	 */
	@OnEvent("receiveMsg2")
	// sendMsg
	public void onEvent(SocketIOClient socketClient, MessageDTO messageDTO) {

		System.out.println(messageDTO);

		// 获取前端传入的接收消息用户 Id
		String toUserId = messageDTO.getToUserId();
		// 客户端 SessionId
		UUID sessionId = clientMap.get(toUserId);
		// 获取前端传入的消息类型
		String msgType = messageDTO.getMsgType();
		// 获取前端传入的当前登录用户 Id
		String userId = messageDTO.getUserId();
		// 获取前端传入的被操作对象 Id
		String beOperatedId = messageDTO.getBeOperatedId();

		// 如果 SessionId 相同,表示用户在线,发送即时通知,用户每次打开 APP 都会生成新的 SessionId
		if (sessionId.equals(socketClient.getSessionId())) {
			if (msgType.equals(MsgTypeEnum.LIKE.getValue())) {
				// socketIOServer.getClient(sessionId).sendEvent("receiveMsg", "你有一条认同消息");
				sendMsgOnEvent(sessionId, "receiveMsg", "你有一条认同消息");
			} else if (msgType.equals(MsgTypeEnum.FOLLOW.getValue())) {
				// socketIOServer.getClient(sessionId).sendEvent("receiveMsg", "你有一条关注消息");
				sendMsgOnEvent(sessionId, "receiveMsg", "你有一条关注消息");
			} else if (msgType.equals(MsgTypeEnum.COMMENT.getValue())) {
				// socketIOServer.getClient(sessionId).sendEvent("receiveMsg", "你有一条评论消息");
				sendMsgOnEvent(sessionId, "receiveMsg", "你有一条评论消息");
			} else {
				// System.out.println("消息类型不匹配");
				StaticLog.info("onEvent 消息类型不匹配");
			}
		}
	}

	/**
	 * botManFromHTML：   接收前端消息,方法名需与前端一致
	 * botManFromServer：前端接收后端发送数据的方法，方法名需与前端一致
	 * <p>
	 * for | http://localhost:9528 使用的
	 *
	 * @param socketClient socketClient
	 * @param thisData     前端发送的数据
	 */
	@OnEvent("botManFromHTML")
	public void onEvent2(SocketIOClient socketClient, String thisData) {

//		 System.out.println(socketClient);

		// 可以 set get
//		 socketClient.set("key1","data1");
//		 System.out.println(socketClient.get("key1").toString());

//		 System.out.println(socketClient.getSessionId());
//		 System.out.println(socketClient.getRemoteAddress());
//		 System.out.println(socketClient.getAllRooms());
//		 System.out.println(socketClient.getTransport()); // POLLING or WEBSOCKET
//		 System.out.println(socketClient.getNamespace().getName());
//		 HandshakeData handshakeData = socketClient.getHandshakeData();

		// Console.log("接收到了前端发送的消息【{}】onEvent2 botManFromHTML thisSessionId {}", thisData, socketClient.getSessionId());
		StaticLog.info("接收到了前端发送的消息【{}】onEvent2 botManFromHTML thisSessionId {}", thisData, socketClient.getSessionId());

		// 还是使用原来的写法吧 目前一个用户名只会有效上线一次了 不会有下面的顾虑了 知道这个意思就行
//		// 开多个浏览器 同一个用户名 应该从缓存中拿取 SessionId 才可以保证同一个用户名只能有合法的一个SessionId 其消息传输也会唯一
//		// 使用 getUserIdBySocketIOClient() 和 getSessionIdByUserId() 来完成此操作
//		// 新写法 保证同一个用户名从缓存中拿取那合法的唯一的一个SessionId
//		UUID thisSessionIdOfUserIdFromCache = getSessionIdByUserId(getUserIdBySocketIOClient(socketClient));
//		// 用一个三目运算符
//		// 当从缓存中获取来的 SessionId 是空 则直接使用 当前socket中传来的 通过【socketClient.getSessionId()】获取
//		socketIOServer.getClient(thisSessionIdOfUserIdFromCache != null ? thisSessionIdOfUserIdFromCache : socketClient.getSessionId()).sendEvent("message", "onEvent2 ---A---" + thisSessionIdOfUserIdFromCache);
//		socketIOServer.getClient(thisSessionIdOfUserIdFromCache != null ? thisSessionIdOfUserIdFromCache : socketClient.getSessionId()).sendEvent("botManFromServer", "onEvent2 ---B---");
//		// 消息发送成功 或 失败 data 为 1 是成功 其他为失败
//		socketIOServer.getClient(thisSessionIdOfUserIdFromCache != null ? thisSessionIdOfUserIdFromCache : socketClient.getSessionId()).sendEvent("ack", "1");

		// 保留原来的写法 使用
		// socketIOServer.getClient(socketClient.getSessionId()).sendEvent("message", "onEvent2 ---A&B---" + socketClient.getSessionId());
		// sendMsgOnEvent(socketClient.getSessionId(), "message", "onEvent2 ---A&B---" + socketClient.getSessionId());

		// 客户端发送什么 转发出去什么
		// socketIOServer.getClient(socketClient.getSessionId()).sendEvent("botManFromServer", "onEvent2 ---B---");
		sendMsgOnEvent(socketClient.getSessionId(), "botManFromServer", getUserIdBySocketIOClient(socketClient) + " 发消息 => " + thisData);

		// 消息发送成功 或 失败 data 为 1 是成功 其他为失败
		// socketIOServer.getClient(socketClient.getSessionId()).sendEvent("ack", "1");
		sendMsgOnEvent(socketClient.getSessionId(), "ack", "1");
		// 保留原来的写法 使用
	}

	/**
	 * botManFromHTMLToWho：   接收前端消息,方法名需与前端一致 指定一个 userId 进行发送
	 * botManFromServer：前端接收后端发送数据的方法，方法名需与前端一致
	 *
	 * @param socketClient socketClient
	 * @param thisData     前端发送的数据 发送格式 ToUserID#sendData 例如 shviplc#hello world
	 */
	@OnEvent("botManFromHTMLToWho")
	public void onEvent3(SocketIOClient socketClient, String thisData) {

		// Console.log("接收到了前端发送的消息 {} onEvent3 botManFromHTMLToWho thisSessionId {}", thisData, socketClient.getSessionId());
		StaticLog.info("接收到了前端发送的消息 {} onEvent3 botManFromHTMLToWho thisSessionId {}", thisData, socketClient.getSessionId());

		// 谁发来的 类似 ahviplc
		String userId = socketClient.getHandshakeData().getSingleUrlParam("userId");

		String[] strings = thisData.split("#");
		// 发给谁 类似 shviplc
		String ToUserID = strings[0];
		// 发送的数据
		String sendData = strings[1];

		// 看 clientMap 包不包含 ToUserID 这个key 不包含 代表没有上线过
		if (clientMap.containsKey(ToUserID)) {
			// socketIOServer.getClient(clientMap.get(ToUserID)).sendEvent("botManFromServer", userId + " 收到了你的数据 " + sendData);
			sendMsgOnEvent(clientMap.get(ToUserID), "botManFromServer", userId + " 收到了你的数据 " + sendData);
		}
	}

	/**
	 * botManChatFromHTML：   接收前端消息,方法名需与前端一致
	 * botManChatFromServer：前端接收后端发送数据的方法，方法名需与前端一致
	 * <p>
	 * for | http://localhost:9528/chat 使用的
	 *
	 * @param socketClient socketClient
	 * @param thisData     前端发送的数据
	 */
	@OnEvent("botManChatFromHTML")
	public void onEvent4(SocketIOClient socketClient, String thisData) {
		// socketIOServer.getClient(socketClient.getSessionId()).sendEvent("botManChatFromServer", thisData);
		sendMsgOnEvent(socketClient.getSessionId(), "botManChatFromServer", getUserIdBySocketIOClient(socketClient) + " 发消息 => " + thisData);
	}

	/**
	 * 监听缓存消息的发送状态的消息事件 onEventMsgSendStatus | 接收带有结构体的消息数据 结构体消息数据
	 * <p>
	 * onEventMsgSendStatus：接收前端消息,方法名需与前端一致
	 * onEventMsgSendStatusToHTML：前端接收后端发送数据的方法，方法名需与前端一致
	 * <p>
	 * for | http://localhost:9528 使用的
	 *
	 * @param socketClient   socketClient
	 * @param thisMessageDTO 前端发送的数据 obj
	 */
	@OnEvent("onEventMsgSendStatus")
	public void onEventMsgSendStatus(SocketIOClient socketClient, MessageDTO thisMessageDTO) {
		StaticLog.info("接收到了前端发送的消息 原数据再次传回到后端服务器 为了用此key清除缓存中的消息数据【{}】onEventMsgSendStatus onEventMsgSendStatus | thisSessionId => {}", thisMessageDTO, socketClient.getSessionId());

		// 看看此缓存的待发
		// 创建弱引用缓存
		// WeakCache<Object, Object> weakCache = thisMyCache.createWeakCacheManager();
		// 创建FIFO(first in first out) 先进先出缓存
		FIFOCache<Object, Object> weakCache = thisMyCache.createFIFOCacheManager();
		if (weakCache.size() == 0) {
			// Console.log("===此待发weakCache数据中 无待发消息===");
			StaticLog.info("===此待发weakCache数据中 无待发消息 无可以清除的缓存消息了===");
			return;
		}

		// 走到这里 代表还有缓存消息
		// 将此key从thisMessageDTO取出
		String thisKeyForRemove = thisMessageDTO.getBeOperatedId();
		if (weakCache.containsKey(thisKeyForRemove)) { // 看缓存中 此key有没有 有的话 remove
			// 将下发成功的 thisKey 从缓存中移除
			weakCache.remove(thisKeyForRemove);
			// 结构体消息数据 下发
			// socketIOServer.getClient(socketClient.getSessionId()).sendEvent("onEventMsgSendStatusToHTML", MyIOUtils.RealTimeMessage(" 缓存消息已清除实时通知 此key为 => " + thisMessageDTO.getBeOperatedId()));
			sendMsgOnEvent(socketClient.getSessionId(), "onEventMsgSendStatusToHTML", MyIOUtils.RealTimeMessage(" 缓存消息已清除实时通知 此key为 => " + thisMessageDTO.getBeOperatedId()));
		}
	}

	/**
	 * botManFromHTMLWithObj | 接收带有结构体的消息数据 结构体消息数据
	 * <p>
	 * botManFromHTMLWithObj：接收前端消息,方法名需与前端一致
	 * botManFromServerWithObj：前端接收后端发送数据的方法，方法名需与前端一致
	 * <p>
	 * for | http://localhost:9528 使用的
	 *
	 * @param socketClient   socketClient
	 * @param thisMessageDTO 前端发送的数据 obj
	 */
	@OnEvent("botManFromHTMLWithObj")
	public void onEventWithObj(SocketIOClient socketClient, MessageDTO thisMessageDTO) {
		StaticLog.info("接收到了前端发送的消息【{}】onEventWithObj botManFromHTMLWithObj | thisSessionId => {}", thisMessageDTO, socketClient.getSessionId());
		// 结构体消息数据 下发
		// socketIOServer.getClient(socketClient.getSessionId()).sendEvent("botManFromServerWithObj", MyIOUtils.RealTimeMessage("我是Obj实时消息 => " + thisMessageDTO.getMsgContent()));
		sendMsgOnEvent(socketClient.getSessionId(), "botManFromServerWithObj", MyIOUtils.RealTimeMessage(getUserIdBySocketIOClient(socketClient) + " 发消息 => " + " 我是Obj实时消息 => " + thisMessageDTO.getMsgContent()));
	}

	/**
	 * 群发 | 群聊
	 * botManFromHTMLWithGroup | 接收带有结构体的消息数据 结构体消息数据
	 * <p>
	 * botManFromHTMLWithGroup：接收前端消息,方法名需与前端一致
	 * botManFromServerWithGroup：前端接收后端发送数据的方法，方法名需与前端一致
	 * <p>
	 * for | http://localhost:9528 使用的 | 群发按钮
	 *
	 * @param socketClient   socketClient
	 * @param thisMessageDTO 前端发送的数据 obj
	 */
	@OnEvent("botManFromHTMLWithGroup")
	public void onEventWithGroup(SocketIOClient socketClient, MessageDTO thisMessageDTO) {
		StaticLog.info("接收到了前端发送的消息【{}】onEventWithGroup botManFromHTMLWithGroup | thisSessionId => {}", thisMessageDTO, socketClient.getSessionId());
		// 结构体消息数据 下发
		sendMsgOnEventByBroadcastOperations("botManFromServerWithGroup", MyIOUtils.RealTimeMessage(getUserIdBySocketIOClient(socketClient) + " 群发实时消息 => " + thisMessageDTO.getMsgContent()));
	}

	// ======================================== 通用方法 ========================================

	/**
	 * 通过 client 获取 userId
	 *
	 * @param client 客户端连接过来的 SocketIOClient
	 * @return 没有的话 返回 null
	 */
	private String getUserIdBySocketIOClient(SocketIOClient client) {
		return client.getHandshakeData().getSingleUrlParam("userId");
	}

	/**
	 * 通过  userId 获取 SessionId
	 *
	 * @param userId 用户ID
	 * @return 没有的话 返回 null
	 */
	private UUID getSessionIdByUserId(String userId) {
		// 在 clientMap 获取
		if (clientMap.containsKey(userId)) {
			return clientMap.get(userId);
		}
		return null;
	}

	/**
	 * 获取连接的客户端ip地址
	 *
	 * @param client: 客户端
	 * @return 得到客户端ip地址
	 */
	private String getIpByClient(SocketIOClient client) {
		String sa = client.getRemoteAddress().toString();
		if (client.getHandshakeData().getHttpHeaders().get("x-forwarded-for") != null) {
			return client.getHandshakeData().getHttpHeaders().get("x-forwarded-for");
		}
		// return sa.substring(1, sa.indexOf(":"));
		return sa;
	}

	/**
	 * 发送上下线通知 | 群发消息 消息数据对象 MessageDTO
	 *
	 * @param messageDTO messageDTO
	 */
	private void sendNotice(MessageDTO messageDTO) {
		if (messageDTO != null) {
			System.out.println(clientMap);
			// 全部发送
			clientMap.forEach((key, value) -> {
				if (value != null) {
					// socketIOServer.getClient(value).sendEvent("receiveMsg", messageDTO); // receiveMsg 此发送事件在前端还未定义
					sendMsgOnEvent(value, "receiveMsg", messageDTO);
				}
			});
		}
	}

	/**
	 * 发送上下线通知 | botManFromServer | 群发消息 字符串消息
	 *
	 * @param messageData 字符串消息
	 */
	private void sendNotice(String messageData) {
		if (messageData != null) {
			StaticLog.info("===群发上线通知===");
			StaticLog.info("===此群发上线通知 => {}", JSONUtil.toJsonStr(clientMap));
			StaticLog.info("===群发上线通知===");
			// 全部发送
			clientMap.forEach((key, value) -> {
				if (value != null) {
					// socketIOServer.getClient(value).sendEvent("botManFromServer", messageData);
					sendMsgOnEvent(value, "botManFromServer", messageData);
				}
			});
		}
	}

	/**
	 * 通过事件名称发送 消息数据对象 消息数据到客户端
	 *
	 * @param thisUUID       此为sessionId
	 * @param eventName      事件名称
	 * @param thisMessageDTO 发送的 消息数据对象 数据
	 */
	private void sendMsgOnEvent(UUID thisUUID, String eventName, MessageDTO thisMessageDTO) {
		socketIOServer.getClient(thisUUID).sendEvent(eventName, thisMessageDTO);
	}

	/**
	 * 通过事件名称发送 字符串 消息数据到客户端
	 *
	 * @param thisUUID        此为sessionId
	 * @param eventName       事件名称
	 * @param thisMessageData 发送的 字符串 数据
	 */
	private void sendMsgOnEvent(UUID thisUUID, String eventName, String thisMessageData) {
		socketIOServer.getClient(thisUUID).sendEvent(eventName, thisMessageData);
	}

	/**
	 * 广播 | 广播操作
	 * 通过事件名称发送 字符串 消息数据到客户端
	 *
	 * @param eventName      事件名称
	 * @param thisMessageDTO 发送的 消息数据对象 数据
	 */
	private void sendMsgOnEventByBroadcastOperations(String eventName, MessageDTO thisMessageDTO) {
		socketIOServer.getBroadcastOperations().sendEvent(eventName, thisMessageDTO);
	}

	/**
	 * 广播 | 广播操作
	 * 通过事件名称发送 字符串 消息数据到客户端
	 *
	 * @param eventName       事件名称
	 * @param thisMessageData 发送的 字符串 数据
	 */
	private void sendMsgOnEventByBroadcastOperations(String eventName, String thisMessageData) {
		socketIOServer.getBroadcastOperations().sendEvent(eventName, thisMessageData);
	}

	// ======================================== 通用方法 ========================================
}
