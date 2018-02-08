package ru.fedrbodr.exchangearbitr.services;

import ru.fedrbodr.exchangearbitr.model.dao.UniSymbol;

public interface SymbolService {
	/**  */
	UniSymbol getOrCreateNewSymbol(String baseCurrency, String marketCurrency);

}
