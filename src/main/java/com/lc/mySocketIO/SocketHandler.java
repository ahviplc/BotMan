package com.lc.mySocketIO;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
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
 * Socket 处理器
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
		this.socketIOServer = server;
	}

	public Map<String, UUID> getClientMap() {
		return this.clientMap;
	}

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
			// TODO
			// userService.queryUserById(userId) != null
			if (true) {
				System.out.println("用户{" + userId + "}开启长连接通知, NettySocketSessionId: {"
						+ socketClient.getSessionId().toString() + "},NettySocketRemoteAddress: {"
						+ socketClient.getRemoteAddress().toString() + "}");
				// 保存
				clientMap.put(userId, socketClient.getSessionId());
				Console.log("保存clientMap成功 最新数据===>>>>>{}", JSONUtil.toJsonStr(clientMap));
				// 发送上线通知
				// this.sendNotice(new MessageDTO(userId, null, null, MsgStatusEnum.ONLINE.getValue()));
				this.sendNotice(userId + " 上线了 ... ");
				// 广播消息
				socketIOServer.getBroadcastOperations().sendEvent("botManFromServer", " 广播消息 " + userId + " 你真的上来了");
				// 单独发 测试
				socketIOServer.getClient(socketClient.getSessionId()).sendEvent("botManFromServer", userId + " 你真的上来了");
				socketIOServer.getClient(socketClient.getSessionId()).sendEvent("message", userId + " 你真的上来了2");
			}
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
			System.out.println("用户{" + userId + "}断开长连接通知, NettySocketSessionId: {"
					+ socketClient.getSessionId().toString() + "},NettySocketRemoteAddress: {"
					+ socketClient.getRemoteAddress().toString() + "}");

			// 发送下线通知
			// this.sendNotice(new MessageDTO(userId, null, null, MsgStatusEnum.OFFLINE.getValue()));

			UUID thisSessionId = clientMap.get(userId);
			Console.log("thisSessionId ===>>>>>{}", thisSessionId);

			SocketIOClient clientIsHave = socketIOServer.getClient(thisSessionId);
			if (clientIsHave == null) {
				Console.log("thisSessionId {} 对应的 SocketIOClient 已经 disconnect ...", thisSessionId);
				// 移除
				clientMap.remove(userId);
				Console.log("thisSessionId {} 在clientMap移除成功 最新数据===>>>>>{}", thisSessionId, JSONUtil.toJsonStr(clientMap));
			}
// 此时 SocketIOClient 为空 就不可以继续下发 sendEvent 了 不可发送消息了
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
					socketIOServer.getClient(value).sendEvent("receiveMsg", messageDTO);
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
			System.out.println("======群发======");
			System.out.println(JSONUtil.toJsonStr(clientMap));
			System.out.println("======群发======");
			// 全部发送
			clientMap.forEach((key, value) -> {
				if (value != null) {
					socketIOServer.getClient(value).sendEvent("botManFromServer", messageData);
				}
			});
		}
	}

	/**
	 * 往 receiveMsg 发的话 简单的string字符串 不可逆向为 MessageDTO 会报错
	 * sendMsg：   接收前端消息,方法名需与前端一致
	 * receiveMsg：前端接收后端发送数据的方法，方法名需与前端一致
	 *
	 * @param socketClient socketClient
	 * @param messageDTO   messageDTO
	 */
	@OnEvent("receiveMsg")
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
				System.out.println("消息类型不匹配");
			}
		}
	}


	/**
	 * botManFromHTML：   接收前端消息,方法名需与前端一致
	 * botManFromServer：前端接收后端发送数据的方法，方法名需与前端一致
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

		Console.log("接收到了前端发送的消息 {} OnEvent botManFromHTML thisSessionId {}", thisData, socketClient.getSessionId());
		socketIOServer.getClient(socketClient.getSessionId()).sendEvent("message", "onEvent2 ---A---");
		socketIOServer.getClient(socketClient.getSessionId()).sendEvent("botManFromServer", "onEvent2 ---B---");
		// 消息发送成功 或 失败 data 为 1 是成功 其他为失败
		socketIOServer.getClient(socketClient.getSessionId()).sendEvent("ack", "1");
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

		Console.log("接收到了前端发送的消息 {} OnEvent botManFromHTMLToWho thisSessionId {}", thisData, socketClient.getSessionId());

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