package ru.fedrbodr.exchangearbitr.dao.shorttime.repo;

import java.util.List;

public interface MarketPositionFastRepositoryCustom {
	List selectTopAfter10MarketPositionFastCompareList();

	List<Object[]> selectFullMarketPositionFastCompareList();

	List<Object[]> selectTopMarketPositionFastCompareList();

	List<Object[]> selectTopProblemMarketPositionFastCompareList();
}
