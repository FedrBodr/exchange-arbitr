package ru.fedrbodr.exchangearbitr.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.ExchangeMetaLong;
import ru.fedrbodr.exchangearbitr.dao.longtime.repo.ExchangeMetaLongRepository;

import javax.annotation.PostConstruct;

@Service
public class ExchangeMetaLongService {
	@Autowired
	private ExchangeMetaLongRepository exchangeMetaLongRepository;

	@PostConstruct
	private void init(){
		exchangeMetaLongRepository.save(ExchangeMetaLong.BITTREX);
		exchangeMetaLongRepository.save(ExchangeMetaLong.POLONIEX);
		exchangeMetaLongRepository.save(ExchangeMetaLong.COINEXCHANGE);
		exchangeMetaLongRepository.save(ExchangeMetaLong.BINANCE);
		exchangeMetaLongRepository.save(ExchangeMetaLong.HITBTC);
		exchangeMetaLongRepository.save(ExchangeMetaLong.KUCOIN);
		exchangeMetaLongRepository.save(ExchangeMetaLong.CRYPTOPIA);
		exchangeMetaLongRepository.save(ExchangeMetaLong.KUNA);
		exchangeMetaLongRepository.save(ExchangeMetaLong.LIVECOIN);
	}

	public ExchangeMetaLong getPersistencedExchangeMetaLong(Integer id) {
		return exchangeMetaLongRepository.findById(id);
	}

}
