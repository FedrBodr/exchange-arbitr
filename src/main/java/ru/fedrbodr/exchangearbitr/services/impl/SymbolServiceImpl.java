package ru.fedrbodr.exchangearbitr.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.fedrbodr.exchangearbitr.dao.SymbolPairRepository;
import ru.fedrbodr.exchangearbitr.dao.model.SymbolPair;
import ru.fedrbodr.exchangearbitr.services.SymbolService;

@Service
public class SymbolServiceImpl implements SymbolService {
	@Autowired
	private SymbolPairRepository symbolPairRepository;

	@Override
	@Transactional
	@Cacheable("symbolByNameBaseQuote")
	public SymbolPair getOrCreateNewSymbol(String baseCurrency, String quoteCurrency) {
		String symbolName = baseCurrency + "-" + quoteCurrency;
		SymbolPair symbolPair = symbolPairRepository.findByName(symbolName);
		if(symbolPair == null){
			symbolPair = symbolPairRepository.saveAndFlush(new SymbolPair(symbolName, baseCurrency, quoteCurrency));
		}
		return symbolPair;
	}


}
