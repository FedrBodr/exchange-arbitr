package ru.fedrbodr.exchangearbitr.services;

import ru.fedrbodr.exchangearbitr.dao.longtime.domain.SymbolLong;

import java.util.List;

public interface SymbolLongService {
	/**  */
	SymbolLong getOrCreateNewSymbol(String baseCurrency, String marketCurrency);

	List<SymbolLong> getAll();
}
