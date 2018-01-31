package ru.fedrbodr.exchangearbitr.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.fedrbodr.exchangearbitr.dao.MarketSummaryRepository;
import ru.fedrbodr.exchangearbitr.model.Symbol;
import ru.fedrbodr.exchangearbitr.services.MarketSummaryService;

@Service
public class MarketSummaryServiceImpl implements MarketSummaryService {
	@Autowired
	private MarketSummaryRepository marketSummaryRepository;

	@Override
	@Transactional
	public Symbol getOrCreateNewSymbol(String marketName, String baseCurrency, String marketCurrency) {
		Symbol symbol = marketSummaryRepository.findByName(marketName);
		if(symbol == null){
			symbol = marketSummaryRepository.saveAndFlush(new Symbol(marketName, baseCurrency, marketCurrency));
		}
		return symbol;
	}

	@Override
	@Cacheable("symbolByName")
	public Symbol getOrCreateNewSymbol(String symbolName) {
		return this.getOrCreateNewSymbol(symbolName, null, null);
	}
}
