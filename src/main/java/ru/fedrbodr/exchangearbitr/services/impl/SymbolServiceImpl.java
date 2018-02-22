package ru.fedrbodr.exchangearbitr.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.Symbol;
import ru.fedrbodr.exchangearbitr.dao.shorttime.repo.SymbolRepository;
import ru.fedrbodr.exchangearbitr.services.SymbolService;

@Service
public class SymbolServiceImpl implements SymbolService {
	@Autowired
	private SymbolRepository symbolRepository;

	@Override
	@Transactional
	@Cacheable("symbolByNameBaseQuote")
	public Symbol getOrCreateNewSymbol(String baseCurrency, String quoteCurrency) {
		String symbolName = baseCurrency + "-" + quoteCurrency;
		Symbol symbol = symbolRepository.findByName(symbolName);
		if(symbol == null){
			symbol = symbolRepository.saveAndFlush(new Symbol(symbolName, baseCurrency, quoteCurrency));
		}
		return symbol;
	}


}
