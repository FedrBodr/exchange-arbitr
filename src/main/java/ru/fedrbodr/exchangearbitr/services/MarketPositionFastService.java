package ru.fedrbodr.exchangearbitr.services;

import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.MarketPositionFast;
import ru.fedrbodr.exchangearbitr.dao.shorttime.reports.DepoFork;
import ru.fedrbodr.exchangearbitr.model.MarketPositionFastCompare;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public interface MarketPositionFastService {
	List<MarketPositionFastCompare> getTopAfter10MarketPositionFastCompareList();

	List<MarketPositionFastCompare> getTopProblemAfterMarketPositionFastCompareList();

	List<MarketPositionFastCompare> getTopFullMarketPositionFastCompareList();

	List<MarketPositionFastCompare> getTopMarketPositionFastCompareList();

	List<MarketPositionFastCompare> getMarketPositionFastCompares();

	List<DepoFork> getDepoForks(BigDecimal deposit);

	Set<MarketPositionFast> getMarketPositionSetToLoadOrderBooks();
}
