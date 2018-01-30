package ru.fedrbodr.exchangearbitr.services.impl;

import com.sun.istack.internal.Nullable;
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
	public Symbol getOrCreateNewMarketSummary(String marketName, @Nullable String baseCurrency, @Nullable String marketCurrency) {
		Symbol symbol = marketSummaryRepository.findByName(marketName);
		/* TODO REFACTOR TO INIT SEPARATELY AND THEN USE*/
		if(symbol == null){
			symbol = saveNewMarketSummary(marketName, null, null);
		}
		return symbol;
	}

	@Override
	@Cacheable("marketSummariesByName")
	public Symbol getOrCreateNewMarketSummary(String marketName) {
		return this.getOrCreateNewMarketSummary(marketName, null, null);
	}

	private Symbol saveNewMarketSummary(String marketName, String baseCurrency, String marketCurrency) {
		Symbol foundedSymbol = new Symbol(marketName, baseCurrency, marketCurrency);
		marketSummaryRepository.saveAndFlush(foundedSymbol);
		return foundedSymbol;
	}
}
