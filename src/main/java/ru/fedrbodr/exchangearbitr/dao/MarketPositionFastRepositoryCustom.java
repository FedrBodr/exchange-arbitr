package ru.fedrbodr.exchangearbitr.dao;

import java.util.List;

public interface MarketPositionFastRepositoryCustom {
	List getTopAfter10MarketPositionFastCompareList();

	List<Object[]> getTopFullMarketPositionFastCompareList();

	List<Object[]> getTop30MarketPositionFastCompareList();

}
