package ru.fedrbodr.exchangearbitr.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import ru.fedrbodr.exchangearbitr.services.ForkService;

@Configuration
@EnableCaching
@EnableScheduling
public class CachingConfig {
	@Autowired
	private ForkService forkService;
	public static final String ORDER_LIST_CACHE = "orderListCache";
	public static final String TOP_AFTER_10_COMPARE_LIST = "topAfter12CompareListCache";
	public static final String TOP_PROBLEM_AFTER_10_COMPARE_LIST = "topProblemAfter12CompareListCache";
	public static final String TOP_COMPARE_LIST = "top30CompareListCache";
	public static final String CURRENT_FORKS_CACHE = "currentForksCache";
	public static final String FREE_CURRENT_FORKS_CACHE = "freeCurrentForksCache";
	public static final String ALL_SYMBOL_LONG_CACHE = "allSymbolLongCache";

	@CacheEvict(allEntries = true, value = {ORDER_LIST_CACHE})
	@Scheduled(fixedDelay = 1 * 1000, initialDelay = 60 * 1000)
	public void reportCacheEvict() {

	}

	@CacheEvict(allEntries = true, value = {FREE_CURRENT_FORKS_CACHE})
	@Scheduled(fixedDelay = 10 * 60 * 1000, initialDelay = 60 * 1000)
	public void currentForksCacheEvict() {

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

	@CacheEvict(allEntries = true, value = {ALL_SYMBOL_LONG_CACHE})
	@Scheduled(fixedDelay = 2 * 60 * 1000, initialDelay = 5 * 1000)
	public void allSymbolLongCacheEvict() {

	}


}
