package ru.fedrbodr.exchangearbitr.services;

import ru.fedrbodr.exchangearbitr.dao.model.SymbolPair;

public interface SymbolService {
	/**  */
	SymbolPair getOrCreateNewSymbol(String baseCurrency, String marketCurrency);

}
