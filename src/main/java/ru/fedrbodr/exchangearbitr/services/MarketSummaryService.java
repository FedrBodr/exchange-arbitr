package ru.fedrbodr.exchangearbitr.services;

import ru.fedrbodr.exchangearbitr.model.Symbol;

public interface MarketSummaryService {
	Symbol getOrCreateNewSymbol(String marketName, String baseCurrency, String marketCurrency);

	Symbol getOrCreateNewSymbol(String marketName);
}
