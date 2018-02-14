package ru.fedrbodr.exchangearbitr.config;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;

@Configuration
@EnableCaching
@EnableScheduling
public class CachingConfig {
	public static final String ORDER_LIST_CACHE = "orderListCache";

	@CacheEvict(allEntries = true, value = {ORDER_LIST_CACHE})
	@Scheduled(fixedDelay = 1 * 60 * 1000 ,  initialDelay = 60 * 1000)
	public void reportCacheEvict() {
		System.out.println("Flush Cache " + new Date());
	}
}
