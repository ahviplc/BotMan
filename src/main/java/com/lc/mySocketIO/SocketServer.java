package com.lc.mySocketIO;

import cn.hutool.log.StaticLog;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * SpringBoot启动之后执行 SocketServer
 */
@Component
@Order(1)
public class SocketServer implements CommandLineRunner {
	/**
	 * logger | slf4j
	 * 这里不使用 如需要 需导入
	 * import org.slf4j.Logger;
	 * import org.slf4j.LoggerFactory;
	 */
	// private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);

	/**
	 * socketIOServer
	 */
	private final SocketIOServer socketIOServer;

	@Autowired
	public SocketServer(SocketIOServer server) {
		this.socketIOServer = server;
	}

	@Override
	public void run(String... args) {
		StaticLog.info("---------- NettySocket通知服务开始启动 ----------");
		socketIOServer.start();
		StaticLog.info("---------- NettySocket通知服务启动成功 ----------");
	}
}
