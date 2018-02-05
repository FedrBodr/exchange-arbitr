package ru.fedrbodr.exchangearbitr.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.fedrbodr.exchangearbitr.dao.MarketSummaryRepository;
import ru.fedrbodr.exchangearbitr.model.dao.UniSymbol;
import ru.fedrbodr.exchangearbitr.services.MarketSummaryService;

@Service
public class MarketSummaryServiceImpl implements MarketSummaryService {
	@Autowired
	private MarketSummaryRepository marketSummaryRepository;

	@Override
	@Transactional
	public UniSymbol getOrCreateNewSymbol(String marketName, String baseCurrency, String quoteCurrency) {
		UniSymbol uniSymbol = marketSummaryRepository.findByName(marketName);
		if(uniSymbol == null){
			uniSymbol = marketSummaryRepository.saveAndFlush(new UniSymbol(marketName, baseCurrency, quoteCurrency));
		}
		return uniSymbol;
	}

	@Override
	@Cacheable("symbolByName")
	public UniSymbol getOrCreateNewSymbol(String symbolName) {
		return this.getOrCreateNewSymbol(symbolName, null, null);
	}
}
