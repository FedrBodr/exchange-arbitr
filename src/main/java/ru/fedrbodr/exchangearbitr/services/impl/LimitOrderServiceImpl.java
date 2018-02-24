package ru.fedrbodr.exchangearbitr.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.dao.longtime.repo.ForkRepository;
import ru.fedrbodr.exchangearbitr.dao.longtime.repo.LimitOrderRepositoryHistory;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.Symbol;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.UniLimitOrder;
import ru.fedrbodr.exchangearbitr.dao.shorttime.repo.LimitOrderRepository;
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
	private long id=0;

	@Autowired
	private Map<ExchangeMeta, Exchange> exchangeMetaToExchangeMap;
	@Autowired
	private LimitOrderRepository limitOrderRepository;
	@Autowired
	private LimitOrderRepositoryHistory limitOrderRepositoryHistory;
	@Autowired
	private ForkRepository forkRepository;

	@Override
	public void readConvertCalcAndSaveUniOrders(Symbol symbol, ExchangeMeta exchangeMeta, String host, Integer port) {
		Date start = new Date();
		try {
			Exchange exchange = exchangeMetaToExchangeMap.get(exchangeMeta);
			MarketDataService marketDataService = exchange.getMarketDataService();

			/**//*
			ExchangeSpecification exchangeSpec = exchange.getExchangeSpecification();
			exchangeSpec.setProxyHost(host);
			exchangeSpec.setProxyPort(port);

			exchange.applySpecification(exchangeSpec);*/

			if(marketDataService!=null) {
				OrderBook orderBook = marketDataService.getOrderBook(
						SymbolsNamesUtils.getCurrencyPair(symbol.getBaseName(), symbol.getQuoteName()),
						100);
				/* Clear previous order list*/
				limitOrderRepository.deleteByUniLimitOrderPk_ExchangeMetaAndUniLimitOrderPk_Symbol(exchangeMeta, symbol);
				Date orderReadingTimeStamp = new Date();
				List<UniLimitOrder> uniAsks = LimitOrderUtils.convertToUniLimitOrderListWithCalcSums(orderBook.getAsks(), exchangeMeta, symbol, orderReadingTimeStamp);
				limitOrderRepository.save(uniAsks);
				limitOrderRepository.flush();
				List<UniLimitOrder> uniBids = LimitOrderUtils.convertToUniLimitOrderListWithCalcSums(orderBook.getBids(), exchangeMeta, symbol, orderReadingTimeStamp);
				limitOrderRepository.save(uniBids);
				limitOrderRepository.flush();
			}else{
				/* TODO uncomment after COINEXCHANGE added
				log.error("BE CAREFUL Can not found MarketDataService for exchangeMeta : " + exchangeMeta.getExchangeName());*/
			}

		} catch (IOException e) {
			log.error("Exception occurred while getting and saving buyOrderBook for exchange " + exchangeMeta.getExchangeName() +
					" and symbol " + symbol.getName(), e);
		}

		/*log.info("After stop readConvertCalcAndSaveUniOrders total time in milliseconds: {} for exchange: {}", (new Date().getTime() - start.getTime()),
				exchangeMeta.getExchangeName());*/
	}

	/*private List<UniLimitOrderHistory> convertToHistory(List<UniLimitOrder> orders) {
		List<UniLimitOrderHistory> uniLimitOrderHistoryList = new ArrayList<>();

		for (UniLimitOrder order : orders) {
			UniLimitOrderPK uniLimitOrderHistoryPk = order.getUniLimitOrderHistoryPk();
			uniLimitOrderHistoryPk.setId(id);
			id++;
			uniLimitOrderHistoryList.add(new UniLimitOrderHistory(uniLimitOrderHistoryPk, order.getLimitPrice(), order.getOriginalAmount(), order.getTimeStamp(),
					order.getOriginalSum(), order.getFinalSum()));


		}
		return uniLimitOrderHistoryList;
	}*/
}
