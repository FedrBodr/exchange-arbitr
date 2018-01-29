package ru.fedrbodr.exchangearbitr.service.impl;

import com.sun.istack.internal.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.fedrbodr.exchangearbitr.dao.MarketSummaryRepository;
import ru.fedrbodr.exchangearbitr.model.MarketSummary;
import ru.fedrbodr.exchangearbitr.service.MarketSummaryService;

@Service
public class MarketSummaryServiceImpl implements MarketSummaryService {
	@Autowired
	private MarketSummaryRepository marketSummaryRepository;

	@Override
	@Transactional
	public MarketSummary getOrCreateNewMarketSummary(String marketName, @Nullable String baseCurrency, @Nullable String marketCurrency) {
		MarketSummary marketSummary = marketSummaryRepository.findByName(marketName);
		if(marketSummary == null){
			marketSummary = saveNewMarketSummary(marketName, null, null);
		}
		return marketSummary;
	}

	@Override
	public MarketSummary getOrCreateNewMarketSummary(String marketName) {
		return this.getOrCreateNewMarketSummary(marketName, null, null);
	}

	private MarketSummary saveNewMarketSummary(String marketName, String baseCurrency, String marketCurrency) {
		MarketSummary foundedMarketSummary = new MarketSummary(marketName, baseCurrency, marketCurrency);
		marketSummaryRepository.saveAndFlush(foundedMarketSummary);
		return foundedMarketSummary;
	}
}
