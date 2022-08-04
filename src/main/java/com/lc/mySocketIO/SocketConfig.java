package com.lc.mySocketIO;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.Transport;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import com.corundumstudio.socketio.listener.ExceptionListener;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Socket配置类 SocketConfig
 */
@Configuration
public class SocketConfig {
	@Value("${socketIO.host}")
	private String host;

	@Value("${socketIO.port}")
	private Integer port;

	@Value("${socketIO.bossCount}")
	private int bossCount;

	@Value("${socketIO.workCount}")
	private int workCount;

	@Value("${socketIO.allowCustomRequests}")
	private boolean allowCustomRequests;

	@Value("${socketIO.upgradeTimeout}")
	private int upgradeTimeout;

	@Value("${socketIO.pingTimeout}")
	private int pingTimeout;

	@Value("${socketIO.pingInterval}")
	private int pingInterval;

	@Value("${socketIO.maxFramePayloadLength}")
	private int maxFramePayloadLength;

	@Value("${socketIO.maxHttpContentLength}")
	private int maxHttpContentLength;


	/**
	 * SocketIOServer 配置
	 *
	 * @return {@link SocketIOServer}
	 */
	@Bean("socketIOServer")
	public SocketIOServer socketIoServer() {
		com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
		// 配置host 隐掉的话 使用默认的
		// config.setHostname(host);
		// 配置端口
		config.setPort(port);
		// 开启Socket端口复用 可解决重启端口占用问题
		com.corundumstudio.socketio.SocketConfig socketConfig = new com.corundumstudio.socketio.SocketConfig();
		socketConfig.setReuseAddress(true);
		config.setSocketConfig(socketConfig);
		// 连接数大小
		config.setWorkerThreads(workCount);
		// 允许客户请求
		config.setAllowCustomRequests(allowCustomRequests);
		// 协议升级超时时间(毫秒)，默认10秒，HTTP握手升级为ws协议超时时间
		config.setUpgradeTimeout(upgradeTimeout);
		// Ping消息超时时间(毫秒)，默认60秒，这个时间间隔内没有接收到心跳消息就会发送超时事件
		config.setPingTimeout(pingTimeout);
		// Ping消息间隔(毫秒)，默认25秒。客户端向服务器发送一条心跳消息间隔
		config.setPingInterval(pingInterval);
		// 设置HTTP交互最大内容长度
		config.setMaxHttpContentLength(maxHttpContentLength);
		// 设置最大每帧处理数据的长度，防止他人利用大数据来攻击服务器
		config.setMaxFramePayloadLength(maxFramePayloadLength);


		socketConfig.setTcpNoDelay(true);
		socketConfig.setSoLinger(0);

		config.setBossThreads(bossCount);
		//config.setTransports(Transport.WEBSOCKET);
		config.setTransports(Transport.POLLING);
		config.setRandomSession(true);  //default is false

		//鉴权
//		config.setAuthorizationListener(data -> {
//			//socketio可以传参，可以获取到 链接后面的参数 ?username=1&pwd=2
//			String username= data.getSingleUrlParam("username");
//			String pwd= data.getSingleUrlParam("pwd");
//			//这里可以用作链接时的权限判断，返回false就是不允许链接，
//			return true;
//		});

		// 异常
		config.setExceptionListener(new ExceptionListener() {
			@Override
			public void onEventException(Exception e, List<Object> args, SocketIOClient client) {
				e.printStackTrace();
			}

			@Override
			public void onDisconnectException(Exception e, SocketIOClient client) {
				e.printStackTrace();
			}

			@Override
			public void onConnectException(Exception e, SocketIOClient client) {
				e.printStackTrace();
			}

			@Override
			public void onPingException(Exception e, SocketIOClient client) {
				e.printStackTrace();
			}

			@Override
			public boolean exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
				cause.printStackTrace();
				ctx.close();
				return false;
			}
		});

		return new SocketIOServer(config);
	}


	/**
	 * 开启 SocketIOServer 注解支持
	 *
	 * @param socketServer socketServer
	 * @return {@link SpringAnnotationScanner}
	 */
	@Bean
	public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketServer) {
		return new SpringAnnotationScanner(socketServer);
	}
}
