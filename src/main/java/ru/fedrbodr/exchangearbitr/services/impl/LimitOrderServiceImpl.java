package ru.fedrbodr.exchangearbitr.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.dao.LimitOrderRepository;
import ru.fedrbodr.exchangearbitr.dao.LimitOrderRepositoryHistory;
import ru.fedrbodr.exchangearbitr.dao.model.*;
import ru.fedrbodr.exchangearbitr.services.LimitOrderService;
import ru.fedrbodr.exchangearbitr.utils.LimitOrderUtils;
import ru.fedrbodr.exchangearbitr.utils.SymbolsNamesUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class LimitOrderServiceImpl implements LimitOrderService {
	private long id=0;

	@Autowired
	private Map<ExchangeMeta, Exchange> exchangeMetaToExchangeMap;
	@Autowired
	private LimitOrderRepository limitOrderRepository;
	@Autowired
	private LimitOrderRepositoryHistory limitOrderRepositoryHistory;

	@Override
	public void readConvertCalcAndSaveUniOrders(SymbolPair symbolPair, ExchangeMeta exchangeMeta, String host, Integer port) {
		Date start = new Date();
		try {
			Exchange exchange = exchangeMetaToExchangeMap.get(exchangeMeta);
			MarketDataService marketDataService = exchange.getMarketDataService();

			/**/
			ExchangeSpecification exchangeSpec = exchange.getExchangeSpecification();
			exchangeSpec.setProxyHost(host);
			exchangeSpec.setProxyPort(port);

			exchange.applySpecification(exchangeSpec);

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

/*				limitOrderRepositoryHistory.save(convertToHistory(uniAsks));
				limitOrderRepositoryHistory.save(convertToHistory(uniBids));
				limitOrderRepository.flush();*/
			}else{
				/* TODO uncomment after COINEXCHANGE added

				log.error("BE CAREFUL Can not found MarketDataService for exchangeMeta : " + exchangeMeta.getExchangeName());*/
			}

		} catch (IOException e) {
			log.error("Exception occurred while getting and saving buyOrderBook for exchange " + exchangeMeta.getExchangeName() +
					" and symbol pair " + symbolPair.getName(), e);
		}

		/*log.info("After stop readConvertCalcAndSaveUniOrders total time in milliseconds: {} for exchange: {}", (new Date().getTime() - start.getTime()),
				exchangeMeta.getExchangeName());*/
	}

	private List<UniLimitOrderHistory> convertToHistory(List<UniLimitOrder> orders) {
		List<UniLimitOrderHistory> uniLimitOrderHistoryList = new ArrayList<>();

		for (UniLimitOrder order : orders) {
			UniLimitOrderPK uniLimitOrderPk = order.getUniLimitOrderPk();
			uniLimitOrderPk.setId(id);
			id++;
			uniLimitOrderHistoryList.add(new UniLimitOrderHistory(uniLimitOrderPk, order.getLimitPrice(), order.getOriginalAmount(), order.getTimeStamp(),
					order.getOriginalSum(), order.getFinalSum()));


		}
		return uniLimitOrderHistoryList;
	}
}
