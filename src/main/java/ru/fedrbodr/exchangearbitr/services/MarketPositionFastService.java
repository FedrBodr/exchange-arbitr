package ru.fedrbodr.exchangearbitr.services;

import org.springframework.cache.annotation.Cacheable;
import ru.fedrbodr.exchangearbitr.model.MarketPositionFastCompare;

import java.util.List;

import static ru.fedrbodr.exchangearbitr.config.CachingConfig.TOP_PROBLEM_AFTER_10_COMPARE_LIST;

public interface MarketPositionFastService {
	List<MarketPositionFastCompare> getTopAfter10MarketPositionFastCompareList();

	@Cacheable(TOP_PROBLEM_AFTER_10_COMPARE_LIST)
	List<MarketPositionFastCompare> getTopProblemAfter10MarketPositionFastCompareList();

	List<MarketPositionFastCompare> getTopFullMarketPositionFastCompareList();

	List<MarketPositionFastCompare> getTopMarketPositionFastCompareList();
}
