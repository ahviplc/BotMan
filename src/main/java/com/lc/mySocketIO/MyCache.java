package com.lc.mySocketIO;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.WeakCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自定义缓存 使用 hutool 的 WeakCache
 */
@Configuration
public class MyCache {

	WeakCache<Object, Object> weakCache = CacheUtil.newWeakCache(Long.parseLong("0"));

	@Bean
	public WeakCache<Object, Object> createCacheManager() {
		return this.weakCache;
	}
}

