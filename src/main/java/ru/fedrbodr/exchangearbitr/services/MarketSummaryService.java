package ru.fedrbodr.exchangearbitr.services;

import ru.fedrbodr.exchangearbitr.model.Symbol;

public interface MarketSummaryService {
	Symbol getOrCreateNewMarketSummary(String marketName, String baseCurrency, String marketCurrency);

	Symbol getOrCreateNewMarketSummary(String marketName);
}
