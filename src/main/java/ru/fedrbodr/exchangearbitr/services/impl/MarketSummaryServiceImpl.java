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
	@Cacheable("symbolByNameBaseQuote")
	public UniSymbol getOrCreateNewSymbol(String symbolName, String baseCurrency, String quoteCurrency) {
		UniSymbol uniSymbol = marketSummaryRepository.findByName(symbolName);
		if(uniSymbol == null){
			uniSymbol = marketSummaryRepository.saveAndFlush(new UniSymbol(symbolName, baseCurrency, quoteCurrency));
		}
		return uniSymbol;
	}
}
