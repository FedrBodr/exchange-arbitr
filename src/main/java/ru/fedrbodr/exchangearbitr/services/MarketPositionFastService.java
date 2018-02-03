package ru.fedrbodr.exchangearbitr.services;

import ru.fedrbodr.exchangearbitr.model.dao.MarketPositionFastCompare;

import java.util.List;

public interface MarketPositionFastService {
	List<MarketPositionFastCompare> getTopMarketPositionFastCompareList();
}
