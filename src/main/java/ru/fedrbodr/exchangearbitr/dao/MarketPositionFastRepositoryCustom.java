package ru.fedrbodr.exchangearbitr.dao;

import javax.transaction.Transactional;
import java.util.List;

public interface MarketPositionFastRepositoryCustom {
	List getTopAfter12MarketPositionFastCompareList();

	@Transactional
	List<Object[]> getTopFullMarketPositionFastCompareList();

	@Transactional
	List<Object[]> getTopProblemMarketPositionFastCompareList();
}
