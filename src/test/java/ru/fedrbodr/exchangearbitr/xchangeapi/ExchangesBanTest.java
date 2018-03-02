package ru.fedrbodr.exchangearbitr.xchangeapi;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.bittrex.BittrexExchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.hitbtc.v2.HitbtcExchange;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * For testing set downloadCount > 0
*
* */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(initializers=ConfigFileApplicationContextInitializer.class)
public class ExchangesBanTest {
	@Value("#{'${proxy.list}'.split(',')}")
	private List<String> proxyList;
	private int downloadCount = 0;

	@Test
	public void testBittrexBan() {
		getAllCurrencyMarketsIter(BittrexExchange.class.getName());
	}

	@Test
	public void testHitBtcBan() {
		getAllCurrencyMarketsIter(HitbtcExchange.class.getName());
	}

	private void getAllCurrencyMarketsIter(String exchangeClassName) {
		Exchange exchange = ExchangeFactory.INSTANCE.createExchange(exchangeClassName);
		Date allStarDate = new Date();
		List<CurrencyPair> exchangeSymbols = exchange.getExchangeSymbols();
		int threadCount = 30;
		ExecutorService executor;
		List<Exchange> exchangePoll = new ArrayList<>();
		exchangePoll.add(exchange);
		for (String prosyHostIp : proxyList) {
			String[] host0Ip1 = prosyHostIp.split(":");
			exchangePoll.add(getExchangeProxy(host0Ip1[0], Integer.parseInt(host0Ip1[1])));
		}

		int marketDataServiceId = exchangePoll.size()-1;
		log.info("threadCount {}", threadCount);
		for(int i = downloadCount; i>0; i--) {
			executor = Executors.newFixedThreadPool(threadCount);
			Date oneLoadStarDate = new Date();
			for (CurrencyPair exchangeSymbol : exchangeSymbols) {
				MarketDataService marketDataService = exchangePoll.get(marketDataServiceId).getMarketDataService();
				Exchange exchange1 = exchangePoll.get(marketDataServiceId);
				if (marketDataServiceId > 0) {
					marketDataServiceId--;
				} else {
					marketDataServiceId = exchangePoll.size() - 1;
				}

				Callable<Void> tCallable = () -> {
					Date starDate = new Date();
					try {
						marketDataService.getOrderBook(exchangeSymbol, 100);
						if(new Date().getTime() - starDate.getTime() > 1000){
							log.info("Slow get orderBook for symbol " + exchangeSymbol + " ProxyHost " +exchange1.getExchangeSpecification().getProxyHost() +
									" Execution time is "+(new Date().getTime() - starDate.getTime()));
						}
					} catch (Exception e) {
						//log.error(e.getMessage() + " " + exchangeSymbol + " ProxyHost " +exchange1.getExchangeSpecification().getProxyHost(), e);
						log.error("Invalid market execution time: {}", new Date().getTime() - starDate.getTime());
					}

					return null;
				};
				executor.submit(tCallable);
			}

			executor.shutdown();
			try {
				executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			}
			log.info("{} one load all currencies order bok end, execution time in seconds: {}", exchangeClassName, (new Date().getTime() - oneLoadStarDate.getTime()) / 1000);
		}
		log.info("{} get all currencies order bok end, execution time in seconds: {}", exchangeClassName, (new Date().getTime() - allStarDate.getTime()) / 1000);
	}

	private Exchange getExchangeProxy(String proxyHost, int proxyPort) {
		Exchange exchangeViaProxy = ExchangeFactory.INSTANCE.createExchange(BittrexExchange.class.getName());
		ExchangeSpecification exchangeSpec = exchangeViaProxy.getDefaultExchangeSpecification();
		exchangeSpec.setProxyHost(proxyHost);
		exchangeSpec.setProxyPort(proxyPort);
		exchangeViaProxy.applySpecification(exchangeSpec);
		return exchangeViaProxy;
	}

}
