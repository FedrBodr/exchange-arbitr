package ru.fedrbodr.exchangearbitr.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.dao.LimitOrderRepository;
import ru.fedrbodr.exchangearbitr.dao.model.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.dao.model.SymbolPair;
import ru.fedrbodr.exchangearbitr.dao.model.UniLimitOrder;
import ru.fedrbodr.exchangearbitr.services.LimitOrderService;
import ru.fedrbodr.exchangearbitr.utils.LimitOrderUtils;
import ru.fedrbodr.exchangearbitr.utils.SymbolsNamesUtils;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class LimitOrderServiceImpl implements LimitOrderService {

	@Autowired
	private Map<ExchangeMeta, MarketDataService> exchangeIdToMarketDataServiceMap;
	@Autowired
	private LimitOrderRepository limitOrderRepository;

	@Override
	public void readConvertCalcAndSaveUniOrders(SymbolPair symbolPair, ExchangeMeta exchangeMeta) {
		try {
			MarketDataService marketDataService = exchangeIdToMarketDataServiceMap.get(exchangeMeta);
			if(marketDataService!=null) {
				OrderBook orderBook = marketDataService.getOrderBook(
						SymbolsNamesUtils.getCurrencyPair(symbolPair.getBaseName(), symbolPair.getQuoteName()),
						100);
				Date orderReadingTimeStamp = new Date();
				List<UniLimitOrder> uniAsks = LimitOrderUtils.convertToUniLimitOrderListWithCalcSums(orderBook.getAsks(), exchangeMeta, symbolPair, orderReadingTimeStamp);
				limitOrderRepository.save(uniAsks);
				List<UniLimitOrder> uniBids = LimitOrderUtils.convertToUniLimitOrderListWithCalcSums(orderBook.getBids(), exchangeMeta, symbolPair, orderReadingTimeStamp);
				limitOrderRepository.save(uniBids);
				limitOrderRepository.flush();
			}else{
				/* TODO uncomment after COINEXCHANGE added

				log.error("BE CAREFUL Can not found MarketDataService for exchangeMeta : " + exchangeMeta.getExchangeName());*/
			}

		} catch (IOException e) {
			log.error("Exception occurred while getting and saving buyOrderBook for exchange " + exchangeMeta.getExchangeName() +
					" and symbol pair " + symbolPair.getName(), e);
		}
	}
}
