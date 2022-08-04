package com.lc.mySocketIO;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.WeakCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自定义缓存 使用 hutool 的 WeakCache
 *
 * @Configuration其实就是告诉spring，spring容器要怎么配置(怎么去注册bean，怎么去处理bean之间的关系(装配))。
 * @Bean的意思就是，我要获取这个bean的时候，你spring要按照这种方式去帮我获取到这个bean。
 */
@Configuration
public class MyCache {

	WeakCache<Object, Object> weakCache = CacheUtil.newWeakCache(Long.parseLong("0"));

	@Bean
	public WeakCache<Object, Object> createCacheManager() {
		return this.weakCache;
	}
}

