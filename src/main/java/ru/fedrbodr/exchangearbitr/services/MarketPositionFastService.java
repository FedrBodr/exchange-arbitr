package ru.fedrbodr.exchangearbitr.services;

import ru.fedrbodr.exchangearbitr.model.MarketPositionFastCompare;

import java.util.List;

public interface MarketPositionFastService {
	List<MarketPositionFastCompare> getTopAfter12MarketPositionFastCompareList();

	List<MarketPositionFastCompare> getTopFullMarketPositionFastCompareList();

	List<MarketPositionFastCompare> getTopProblemMarketPositionFastCompareList();
}
