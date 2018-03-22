package ru.fedrbodr.exchangearbitr.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.dao.shorttime.repo.ExchangeMetaRepository;

import javax.annotation.PostConstruct;

@Service
public class ExchangeMetaService {
	@Autowired
	private ExchangeMetaRepository exchangeMetaLongRepository;

	@PostConstruct
	private void init() {
		/* TODO move preinit to more convenient place ? */
		exchangeMetaLongRepository.save(ExchangeMeta.BITTREX);
		exchangeMetaLongRepository.save(ExchangeMeta.BINANCE);
		exchangeMetaLongRepository.save(ExchangeMeta.COINEXCHANGE);
		exchangeMetaLongRepository.save(ExchangeMeta.POLONIEX);
		exchangeMetaLongRepository.save(ExchangeMeta.HITBTC);
		exchangeMetaLongRepository.save(ExchangeMeta.KUCOIN);
		exchangeMetaLongRepository.save(ExchangeMeta.CRYPTOPIA);
		exchangeMetaLongRepository.save(ExchangeMeta.KUNA);
		exchangeMetaLongRepository.save(ExchangeMeta.LIVECOIN);

		exchangeMetaLongRepository.flush();
	}

	@Cacheable("exchangeMetaByIdCache")
	public ExchangeMeta getExchangeMetaById(int id) {
		return exchangeMetaLongRepository.findById(id);
	}
}
