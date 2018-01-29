package ru.fedrbodr.exchangearbitr.service;

import ru.fedrbodr.exchangearbitr.model.MarketSummary;

public interface MarketSummaryService {
	MarketSummary getOrCreateNewMarketSummary(String marketName, String baseCurrency, String marketCurrency);

	MarketSummary getOrCreateNewMarketSummary(String marketName);
}
