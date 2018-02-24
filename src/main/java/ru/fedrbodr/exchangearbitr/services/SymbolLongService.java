package ru.fedrbodr.exchangearbitr.services;

import ru.fedrbodr.exchangearbitr.dao.longtime.domain.SymbolLong;

public interface SymbolLongService {
	/**  */
	SymbolLong getOrCreateNewSymbol(String baseCurrency, String marketCurrency);

}
