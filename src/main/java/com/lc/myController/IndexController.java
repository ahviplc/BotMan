package com.lc.myController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 首页控制器
 * <p>
 * 通过Controller控制器层跳转访问的资源
 *
 * @author LC
 **/
@Controller
public class IndexController {

	/**
	 * 首页 - for BotMan - 不是方框风格的表单
	 * bot-man
	 * bot-man-test
	 *
	 * @return
	 */
	@RequestMapping("/")
	public String index() {
		return "bot-man-test"; // bot-man 是vue版本 todo 待做
	}

	/**
	 * 首页 - for BotMan - 方框风格的表单
	 * <p>
	 * bot-man-test-pane
	 *
	 * @return
	 */
	@RequestMapping("/pane")
	public String index_pane() {
		return "bot-man-test-pane";
	}

	/**
	 * chat - for BotMan
	 * <p>
	 * bot-man-chat
	 *
	 * @return
	 */
	@RequestMapping("/chat")
	public String chat() {
		return "bot-man-chat";
	}

	/**
	 * status页
	 *
	 * @return
	 */
	@RequestMapping("/status")
	public String status() {
		return "status";
	}

	/**
	 * index页
	 *
	 * @return
	 */
	@RequestMapping("/index")
	public String index2() {
		return "index";
	}

	/**
	 * layui-vue-index页
	 *
	 * @return
	 */
	@RequestMapping("/layuiVueIndex")
	public String layuiVueIndex() {
		return "layui-vue-index";
	}
}
