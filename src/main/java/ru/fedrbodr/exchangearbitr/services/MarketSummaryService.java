package ru.fedrbodr.exchangearbitr.services;

import ru.fedrbodr.exchangearbitr.model.MarketSummary;

public interface MarketSummaryService {
	MarketSummary getOrCreateNewMarketSummary(String marketName, String baseCurrency, String marketCurrency);

	MarketSummary getOrCreateNewMarketSummary(String marketName);
}
