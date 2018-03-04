package ru.fedrbodr.exchangearbitr.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.dao.shorttime.repo.ExchangeMetaRepository;

@Service
public class ExchangeMetaService {
	@Autowired
	private ExchangeMetaRepository exchangeMetaLongRepository;

	@Cacheable("exchangeMetaByIdCache")
	public ExchangeMeta getExchangeMetaById(int id) {
		return exchangeMetaLongRepository.findById(id);
	}
}
