package ru.fedrbodr.exchangearbitr.services;

import ru.fedrbodr.exchangearbitr.model.dao.UniSymbol;

public interface SymbolService {
	/**
	 * @param symbolName BTC-ETH format name!
	 *
	 * */
	UniSymbol getOrCreateNewSymbol(String symbolName, String baseCurrency, String marketCurrency);

}
