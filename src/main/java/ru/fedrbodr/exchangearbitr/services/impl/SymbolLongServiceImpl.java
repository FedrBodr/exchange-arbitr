package ru.fedrbodr.exchangearbitr.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.SymbolLong;
import ru.fedrbodr.exchangearbitr.dao.longtime.repo.SymbolLongRepository;
import ru.fedrbodr.exchangearbitr.services.SymbolLongService;

import java.util.List;

@Service
public class SymbolLongServiceImpl implements SymbolLongService {
	@Autowired
	private SymbolLongRepository symbolRepository;

	@Override
	@Transactional
	@Cacheable("symbolLOngByNameBaseQuote")
	public SymbolLong getOrCreateNewSymbol(String base, String quote) {

		String symbolName = base + "-" + quote;
		SymbolLong symbol = symbolRepository.findByName(symbolName);
		if(symbol == null){
			symbol = symbolRepository.saveAndFlush(new SymbolLong(symbolName, base, quote));
		}
		return symbol;
	}

	@Override
	@Cacheable("allSymbolLongCache")
	public List<SymbolLong> getAll() {
		return symbolRepository.findAllByOrderByBaseNameAscQuoteNameAsc();
	}
}
