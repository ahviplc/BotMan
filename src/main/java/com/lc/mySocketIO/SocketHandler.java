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
import com.corundumstudio.socketio.listener.PingListener;
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
	 * ConcurrentHashMap 保存当前 SocketServer 用户 Id 对应关系
	 */
	private Map<String, UUID> clientMap = new ConcurrentHashMap<>(16);

	/**
	 * socketIOServer
	 */
	private final SocketIOServer socketIOServer;

	@Autowired
	public SocketHandler(SocketIOServer server) {

		// it can work 这里我设置 4分钟 240秒 客户端ping一下 设置时间越短 ping出错的概率就大
		// 配置点在【application.properties:43】
		// SocketIOServer 新增 ping 的监听器
		server.addPingListener(new PingListener() {
			@Override
			public void onPing(SocketIOClient client) {
				StaticLog.info("Ping来了 getSessionId => {}", client.getSessionId());
				StaticLog.info("Ping来了 getUrlParams 握手数据中的参数 => {}", client.getHandshakeData().getUrlParams());
			}
		});

		this.socketIOServer = server;
	}

	public Map<String, UUID> getClientMap() {
		return this.clientMap;
	}

	@Autowired
	MyCache thisMyCache;

	/**
	 * 获取连接的客户端ip地址
	 *
	 * @param client: 客户端
	 * @return: java.lang.String
	 */
	private String getIpByClient(SocketIOClient client) {
		String sa = client.getRemoteAddress().toString();
		if (client.getHandshakeData().getHttpHeaders().get("x-forwarded-for") != null) {
			return client.getHandshakeData().getHttpHeaders().get("x-forwarded-for");
		}
		return sa.substring(1, sa.indexOf(":"));
	}

	/**
	 * 当客户端发起连接时调用
	 *
	 * @param socketClient socketClient
	 */
	@OnConnect
	public void onConnect(SocketIOClient socketClient) {
		String userId = socketClient.getHandshakeData().getSingleUrlParam("userId");

		// 不可用 不准确
		// System.out.println(getIpByClient(socketClient));

		if (StrUtil.isNotBlank(userId)) {
			// TODO 这里其实就是需要查库或者什么操作 确定用户的真实性 这里不做处理
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
				socketIOServer.getBroadcastOperations().sendEvent("botManFromServer", " 广播消息 " + userId + " 你真的上来了");
				// 单独发 测试
				socketIOServer.getClient(socketClient.getSessionId()).sendEvent("botManFromServer", userId + " 你真的上来了");
				socketIOServer.getClient(socketClient.getSessionId()).sendEvent("message", userId + " 你真的上来了2");
			}

			// 看看有没有缓存的待发
			// 创建弱引用缓存
			// WeakCache<Object, Object> weakCache = thisMyCache.createWeakCacheManager();
			// 创建FIFO(first in first out) 先进先出缓存
			FIFOCache<Object, Object> weakCache = thisMyCache.createFIFOCacheManager();
			if (weakCache.size() == 0) {
				// Console.log("===此待发weakCache数据中 无待发消息===");
				StaticLog.info("===此待发weakCache数据中 无待发消息===");
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
					socketIOServer.getClient(thisSessionId).sendEvent("onEventMsgSendStatusToHTML", MyIOUtils.All(userId, "", thisKey.toString(), MsgTypeEnum.CachedMessage.getValue(), userIdFromCache + " 【cache】 " + jsonObject.get("msgContents").toString()));

					// 将下发成功的 thisKey 从缓存中移除 这部分逻辑放在了 下面的 socket事件 onEventMsgSendStatus 中了
				}
				// 每次睡眠 500 毫秒
				ThreadUtil.safeSleep(500);
			}
			// Console.log("此待发weakCache数据中 {}条待发消息 开始逐步下发 完毕 => {}", weakCache.size(), JSONUtil.toJsonStr(weakCache));
			StaticLog.info("===此待发weakCache数据中 {} 条待发消息 开始逐步下发 完毕 => {}", weakCache.size(), JSONUtil.toJsonStr(weakCache));
		}
	}


	/**
	 * 客户端断开连接时调用，刷新客户端信息
	 *
	 * @param socketClient socketClient
	 */
	@OnDisconnect
	public void onDisConnect(SocketIOClient socketClient) {
		String userId = socketClient.getHandshakeData().getSingleUrlParam("userId");
		if (StrUtil.isNotBlank(userId)) {
			// System.out.println("用户{" + userId + "}断开长连接通知, NettySocketSessionId: {" + socketClient.getSessionId().toString() + "},NettySocketRemoteAddress: {" + socketClient.getRemoteAddress().toString() + "}");
			StaticLog.info("用户 {} 断开长连接通知, NettySocketSessionId: {}, NettySocketRemoteAddress: {}", userId, socketClient.getSessionId().toString(), socketClient.getRemoteAddress().toString());

			// 发送下线通知
			// this.sendNotice(new MessageDTO(userId, null, null, MsgStatusEnum.OFFLINE.getValue()));

			UUID thisSessionId = clientMap.get(userId);
			// Console.log("用户 {} 下线了 此要移除的 thisSessionId => {}", thisSessionId);
			StaticLog.info("用户 {} 下线了 此要移除的 thisSessionId => {}", userId, thisSessionId);

			SocketIOClient clientIsHave = socketIOServer.getClient(thisSessionId);
			if (clientIsHave == null) {
				// Console.log("用户 {} 下线了 此要移除的 thisSessionId {} 对应的 SocketIOClient 已经 disconnect ...", userId, thisSessionId);
				StaticLog.info("用户 {} 下线了 此要移除的 thisSessionId {} 对应的 SocketIOClient 已经 disconnect ...", userId, thisSessionId);
				// 移除
				clientMap.remove(userId);
				// Console.log("用户 {} 下线了 此要移除的 thisSessionId {} 在clientMap移除成功 最新数据 => {}", userId, thisSessionId, JSONUtil.toJsonStr(clientMap));
				StaticLog.info("用户 {} 下线了 此要移除的 thisSessionId {} 在clientMap移除成功 最新数据 => {}", userId, thisSessionId, JSONUtil.toJsonStr(clientMap));
			}

//          此时 SocketIOClient 为空 就不可以继续下发 sendEvent 了 不可发送消息了
//			socketIOServer.getClient(thisSessionId).sendEvent("botManFromServer", "你真的走了");
//			socketIOServer.getClient(thisSessionId).sendEvent("message", "你真的走了");

		}
	}

	/**
	 * 发送上下线通知
	 *
	 * @param messageDTO messageDTO
	 */
	private void sendNotice(MessageDTO messageDTO) {
		if (messageDTO != null) {
			System.out.println(clientMap);
			// 全部发送
			clientMap.forEach((key, value) -> {
				if (value != null) {
					socketIOServer.getClient(value).sendEvent("receiveMsg", messageDTO); // receiveMsg 此发送事件在前端还未定义
				}
			});
		}
	}


	/**
	 * 发送上下线通知 | botManFromServer
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
					socketIOServer.getClient(value).sendEvent("botManFromServer", messageData);
				}
			});
		}
	}

	/**
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
				socketIOServer.getClient(sessionId).sendEvent("receiveMsg", "你有一条认同消息");
			} else if (msgType.equals(MsgTypeEnum.FOLLOW.getValue())) {
				socketIOServer.getClient(sessionId).sendEvent("receiveMsg", "你有一条关注消息");
			} else if (msgType.equals(MsgTypeEnum.COMMENT.getValue())) {
				socketIOServer.getClient(sessionId).sendEvent("receiveMsg", "你有一条评论消息");
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

		socketIOServer.getClient(socketClient.getSessionId()).sendEvent("message", "onEvent2 ---A---");
		socketIOServer.getClient(socketClient.getSessionId()).sendEvent("botManFromServer", "onEvent2 ---B---");
		// 消息发送成功 或 失败 data 为 1 是成功 其他为失败
		socketIOServer.getClient(socketClient.getSessionId()).sendEvent("ack", "1");
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
		StaticLog.info("接收到了前端发送的消息 原数据再次回到后端服务器 为了用此key请求缓存中的数据【{}】onEventMsgSendStatus onEventMsgSendStatus | thisSessionId => {}", thisMessageDTO, socketClient.getSessionId());

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
			socketIOServer.getClient(socketClient.getSessionId()).sendEvent("onEventMsgSendStatusToHTML", MyIOUtils.RealTimeMessage(" 缓存消息已清除实时通知 此key为 => " + thisMessageDTO.getBeOperatedId()));
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
		socketIOServer.getClient(socketClient.getSessionId()).sendEvent("botManFromServerWithObj", MyIOUtils.RealTimeMessage("我是Obj实时消息 => " + thisMessageDTO.getMsgContent()));
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
		socketIOServer.getClient(socketClient.getSessionId()).sendEvent("botManChatFromServer", thisData);
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
			socketIOServer.getClient(clientMap.get(ToUserID)).sendEvent("botManFromServer", userId + " 收到了你的数据 " + sendData);
		}
	}
}
