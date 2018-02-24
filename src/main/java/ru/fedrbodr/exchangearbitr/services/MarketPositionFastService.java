package ru.fedrbodr.exchangearbitr.services;

import ru.fedrbodr.exchangearbitr.model.MarketPositionFastCompare;

import java.util.List;

public interface MarketPositionFastService {
	List<MarketPositionFastCompare> getTopAfter10MarketPositionFastCompareList();

	List<MarketPositionFastCompare> getTopProblemAfterMarketPositionFastCompareList();

	List<MarketPositionFastCompare> getTopFullMarketPositionFastCompareList();

	List<MarketPositionFastCompare> getTopMarketPositionFastCompareList();

	List<MarketPositionFastCompare> getMarketPositionFastCompares();
}
