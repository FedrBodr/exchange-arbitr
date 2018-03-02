package ru.fedrbodr.exchangearbitr.config;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableCaching
@EnableScheduling
public class CachingConfig {
	public static final String ORDER_LIST_CACHE = "orderListCache";
	public static final String TOP_AFTER_10_COMPARE_LIST = "topAfter12CompareListCache";
	public static final String TOP_PROBLEM_AFTER_10_COMPARE_LIST = "topProblemAfter12CompareListCache";
	public static final String TOP_COMPARE_LIST = "top30CompareListCache";

	@CacheEvict(allEntries = true, value = {ORDER_LIST_CACHE})
	@Scheduled(fixedDelay = 1 * 1000, initialDelay = 60 * 1000)
	public void reportCacheEvict() {

	}

	@CacheEvict(allEntries = true, value = {TOP_AFTER_10_COMPARE_LIST})
	@Scheduled(fixedDelay = 1 * 1000, initialDelay = 30 * 1000)
	public void topAfter10CompareListCacheEvict() {

	}

	@CacheEvict(allEntries = true, value = {TOP_PROBLEM_AFTER_10_COMPARE_LIST})
	@Scheduled(fixedDelay = 1 * 1000, initialDelay = 30 * 1000)
	public void topProblemAfter10CompareListCacheEvict() {

	}

	@CacheEvict(allEntries = true, value = {TOP_COMPARE_LIST})
	@Scheduled(fixedDelay = 1 * 1000, initialDelay = 30 * 1000)
	public void topCompareListCacheEvict() {

	}


}
