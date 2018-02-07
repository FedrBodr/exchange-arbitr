package ru.fedrbodr.exchangearbitr.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.fedrbodr.exchangearbitr.dao.UniSymbolRepository;
import ru.fedrbodr.exchangearbitr.model.dao.UniSymbol;
import ru.fedrbodr.exchangearbitr.services.SymbolService;

@Service
public class SymbolServiceImpl implements SymbolService {
	@Autowired
	private UniSymbolRepository uniSymbolRepository;

	@Override
	@Transactional
	@Cacheable("symbolByNameBaseQuote")
	public UniSymbol getOrCreateNewSymbol(String symbolName, String baseCurrency, String quoteCurrency) {
		UniSymbol uniSymbol = uniSymbolRepository.findByName(symbolName);
		if(uniSymbol == null){
			uniSymbol = uniSymbolRepository.saveAndFlush(new UniSymbol(symbolName, baseCurrency, quoteCurrency));
		}
		return uniSymbol;
	}


}
