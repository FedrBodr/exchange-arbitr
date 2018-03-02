package ru.fedrbodr.exchangearbitr.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.Symbol;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.UniLimitOrder;
import ru.fedrbodr.exchangearbitr.dao.shorttime.repo.LimitOrderRepository;
import ru.fedrbodr.exchangearbitr.services.LimitOrderService;
import ru.fedrbodr.exchangearbitr.utils.LimitOrderUtils;
import ru.fedrbodr.exchangearbitr.utils.SymbolsNamesUtils;
import ru.fedrbodr.exchangearbitr.xchange.custom.ExchangeProxy;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
public class LimitOrderServiceImpl implements LimitOrderService {
	private static final long MAX_TIMEOUT_FOR_GETING_ORDER_BOOK_REQUESTR = 4000;

	@Autowired
	private Map<ExchangeMeta, ExchangeProxy> exchangeMetaToExchangeProxyMap;
	@Autowired
	private LimitOrderRepository limitOrderRepository;

	@Override
	public void readConvertCalcAndSaveUniOrders(Symbol symbol, ExchangeMeta exchangeMeta) throws InterruptedException {
		Date start = new Date();
		MarketDataService marketDataService;
		Exchange exchange = null;
		if(exchangeMeta.equals(ExchangeMeta.COINEXCHANGE)){
			marketDataService = exchangeMetaToExchangeProxyMap.get(exchangeMeta).getNextMarketDataService();
		}else{
			exchange = exchangeMetaToExchangeProxyMap.get(exchangeMeta).getNextExchange();
			marketDataService = exchange.getMarketDataService();
		}

		if (marketDataService != null) {
			try {
				AtomicReference<OrderBook> orderBook = new AtomicReference<>();


				ExecutorService executorService = Executors.newSingleThreadExecutor();

				Callable<Void> tCallable = () -> {
					orderBook.set(marketDataService.getOrderBook(
							SymbolsNamesUtils.getCurrencyPair(symbol.getBaseName(), symbol.getQuoteName()),
							100));
					return null;
				};
				FutureTask futureTask = new FutureTask(tCallable);
				executorService.execute(futureTask);
				executorService.shutdown();

				executorService.awaitTermination(MAX_TIMEOUT_FOR_GETING_ORDER_BOOK_REQUESTR, TimeUnit.MILLISECONDS);

				if (orderBook.get() == null) {
					log.error("LONG getting order bok detected time {} for exchange {} and symbol {} by proxy {}",
							(new Date().getTime() - start.getTime()), exchangeMeta.getExchangeName(), symbol.getName(), exchange==null? "null exchange local ip":exchange.getExchangeSpecification().getProxyHost());
				} else {
					/*TODO refactor to service */
					Date orderReadingTimeStamp = new Date();
					List<UniLimitOrder> uniAsks = LimitOrderUtils.convertToUniLimitOrderListWithCalcSums(orderBook.get().getAsks(), exchangeMeta, symbol, orderReadingTimeStamp, Order.OrderType.ASK);
					/* Clear previous order list*/
					try {
						/*log.info("DELETE on exchange {} {}  and symbol {} {}",
								exchangeMeta.getExchangeName(), exchangeMeta.getId(), symbol.getName(),symbol.getId());*/
						limitOrderRepository.deleteByExchangeMetaAndSymbol(exchangeMeta, symbol);
					}catch (ObjectOptimisticLockingFailureException e){
						// something weird - i am cannot understand where but hibernate persistence scope not equal to the db
						log.error("New ObjectOptimisticLockingFailureException occured while try to deleteByExchangeMetaAndSymbol on exchange {} and symbol {}",
								exchangeMeta.getExchangeName(), symbol.getName());
					}


					limitOrderRepository.save(uniAsks);
					limitOrderRepository.flush();
					List<UniLimitOrder> uniBids = LimitOrderUtils.convertToUniLimitOrderListWithCalcSums(orderBook.get().getBids(), exchangeMeta, symbol, orderReadingTimeStamp, Order.OrderType.BID);
					limitOrderRepository.save(uniBids);
					limitOrderRepository.flush();
				}
			} catch (Exception e) {
				log.error("Exception occurred while getting and saving buyOrderBook for exchange " + exchangeMeta.getExchangeName() +
						" and symbol " + symbol.getName(), e);
				throw new InterruptedException("Some problems with loadinf order book for exchange "
						+ exchangeMeta.getExchangeName() + " and symbol " + symbol.getName());
			}
		} else {
			log.error("BE CAREFUL Can not found MarketDataService for exchangeMeta : " + exchangeMeta.getExchangeName());
		}

		/*log.info("After stop readConvertCalcAndSaveUniOrders total time in milliseconds: {} for exchange: {}", (new Date().getTime() - start.getTime()),
				exchangeMeta.getExchangeName());*/

	}
}
