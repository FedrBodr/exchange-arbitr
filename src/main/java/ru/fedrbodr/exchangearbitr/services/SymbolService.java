package ru.fedrbodr.exchangearbitr.services;

import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.Symbol;

public interface SymbolService {
	/**  */
	Symbol getOrCreateNewSymbol(String baseCurrency, String marketCurrency);

}
