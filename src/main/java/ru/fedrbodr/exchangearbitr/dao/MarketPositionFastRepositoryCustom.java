package ru.fedrbodr.exchangearbitr.dao;

import java.util.List;

public interface MarketPositionFastRepositoryCustom {
	List selectTopAfter10MarketPositionFastCompareList();

	List<Object[]> selectFullMarketPositionFastCompareList();

	List<Object[]> selectTopMarketPositionFastCompareList();

	List<Object[]> selectTopProblemMarketPositionFastCompareList();
}
