package ru.fedrbodr.exchangearbitr.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.ExchangeMetaLong;
import ru.fedrbodr.exchangearbitr.dao.longtime.repo.ExchangeMetaLongRepository;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.ExchangeMeta;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class ExchangeMetaLongService {
	@Autowired
	private ExchangeMetaLongRepository exchangeMetaLongRepository;
	private Map<Integer, ExchangeMetaLong> persistencedExchangeMetaMap;

	@PostConstruct
	private void init(){
		persistencedExchangeMetaMap = new HashMap<>();
		persistencedExchangeMetaMap.put(ExchangeMetaLong.BITTREX.getId(), exchangeMetaLongRepository.save(ExchangeMetaLong.BITTREX));
		persistencedExchangeMetaMap.put(ExchangeMetaLong.POLONIEX.getId(), exchangeMetaLongRepository.save(ExchangeMetaLong.POLONIEX));
		persistencedExchangeMetaMap.put(ExchangeMetaLong.COINEXCHANGE.getId(), exchangeMetaLongRepository.save(ExchangeMetaLong.COINEXCHANGE));
		persistencedExchangeMetaMap.put(ExchangeMetaLong.BINANCE.getId(), exchangeMetaLongRepository.save(ExchangeMetaLong.BINANCE));
		persistencedExchangeMetaMap.put(ExchangeMetaLong.HITBTC.getId(), exchangeMetaLongRepository.save(ExchangeMetaLong.HITBTC));
		persistencedExchangeMetaMap.put(ExchangeMetaLong.KUCOIN.getId(), exchangeMetaLongRepository.save(ExchangeMetaLong.KUCOIN));
		persistencedExchangeMetaMap.put(ExchangeMetaLong.btctrade.getId(), exchangeMetaLongRepository.save(ExchangeMetaLong.btctrade));
	}

	public ExchangeMetaLong getPersistencedExchangeMetaLong(ExchangeMeta exchangeMeta) {
		return persistencedExchangeMetaMap.get(exchangeMeta.getId());
	}

	public ExchangeMetaLong getPersistencedExchangeMetaLong(Integer id) {
		return exchangeMetaLongRepository.findById(id);
	}

}
