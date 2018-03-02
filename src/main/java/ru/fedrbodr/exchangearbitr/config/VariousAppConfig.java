package ru.fedrbodr.exchangearbitr.config;

import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.bittrex.BittrexExchange;
import org.knowm.xchange.hitbtc.v2.HitbtcExchange;
import org.knowm.xchange.kucoin.KucoinExchange;
import org.knowm.xchange.poloniex.PoloniexExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.services.ExchangeReader;
import ru.fedrbodr.exchangearbitr.xchange.custom.CoinexchangeMarketDataService;
import ru.fedrbodr.exchangearbitr.xchange.custom.ExchangeProxy;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Configuration
@EnableCaching
@EnableAsync
@Slf4j
public class VariousAppConfig {
	@Value("#{'${proxy.list}'.split(',')}")
	private List<String> proxyList;

	@Autowired
	@Qualifier("bittrexExchangeReaderImpl")
	private ExchangeReader bittrexExchangeReader;
	@Autowired
	@Qualifier("poloniexExchangeReaderImpl")
	private ExchangeReader poloniexExchangeReader;
	@Autowired
	@Qualifier("coinexchangeExchangeReaderImpl")
	private ExchangeReader coinexchangeExchangeReader;
	@Autowired
	@Qualifier("hitBtcExchangeReaderImpl")
	private ExchangeReader hitBtcExchangeReaderImpl;
	@Autowired
	@Qualifier("kucoinExchangeReaderImpl")
	private ExchangeReader kucoinExchangeReaderImpl;

	@PostConstruct
	public void init() {
		/*TimeZone.setDefault(TimeZone.getTimeZone("GMT+3"));*/
	}

	@Bean(name = "marketSumTaskExecutor")
	public ExecutorService marketSummTaskExecutor() {
		return Executors.newSingleThreadExecutor();
	}

	@Bean(name = "ordersTaskExecutor")
	public ExecutorService ordersTaskExecutor() {
		return Executors.newSingleThreadExecutor();
	}

	@Bean
	public Map<ExchangeMeta, ExchangeProxy> exchangeMetaToExchangeMap(@Autowired CoinexchangeMarketDataService coinexchangeMarketDataService) {
		log.info("Before start initMarketDataServices");
		Map<ExchangeMeta, ExchangeProxy> exchangeMetaToExchangeMap;

		Date startTime = new Date();
		exchangeMetaToExchangeMap = new HashMap<>();
		ExecutorService executorService = Executors.newFixedThreadPool(6);
		Callable<Void> tCallable = () -> {
			exchangeMetaToExchangeMap.put(ExchangeMeta.BITTREX, new ExchangeProxy(proxyList, BittrexExchange.class.getName()));
			return null;
		};
		Callable<Void> tCallable1 = () -> {
			exchangeMetaToExchangeMap.put(ExchangeMeta.BINANCE, new ExchangeProxy(proxyList, BinanceExchange.class.getName()));
			return null;
		};
		Callable<Void> tCallable2 = () -> {
			exchangeMetaToExchangeMap.put(ExchangeMeta.POLONIEX, new ExchangeProxy(proxyList, PoloniexExchange.class.getName()));
			return null;
		};
		Callable<Void> tCallable3 = () -> {
			exchangeMetaToExchangeMap.put(ExchangeMeta.HITBTC, new ExchangeProxy(proxyList, HitbtcExchange.class.getName()));
			return null;
		};
		Callable<Void> tCallable4 = () -> {
			exchangeMetaToExchangeMap.put(ExchangeMeta.KUCOIN, new ExchangeProxy(proxyList, KucoinExchange.class.getName()));
			return null;
		};
		Callable<Void> tCallable5 = () -> {
			exchangeMetaToExchangeMap.put(ExchangeMeta.COINEXCHANGE, new ExchangeProxy(coinexchangeMarketDataService));
			return null;
		};

		executorService.execute(new FutureTask(tCallable));
		executorService.execute(new FutureTask(tCallable1));
		executorService.execute(new FutureTask(tCallable2));
		executorService.execute(new FutureTask(tCallable3));
		executorService.execute(new FutureTask(tCallable4));
		executorService.execute(new FutureTask(tCallable5));

		executorService.shutdown();
		try {
			executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		}

		log.info("After stop initMarketDataServices. time in  seconds: {}", (startTime.getTime() - new Date().getTime()) / 1000);
		return exchangeMetaToExchangeMap;
	}

	@Bean
	public Map<ExchangeMeta, ExchangeReader> exchangeMetaToExchangeSummariesReaderMap() {
		Map<ExchangeMeta, ExchangeReader> exchangeMetaToExchangeSummariesReaderMap = new HashMap<>();
		/*BINANCE NOT NEEDAD in this place*/
		exchangeMetaToExchangeSummariesReaderMap.put(ExchangeMeta.BITTREX, bittrexExchangeReader);
		exchangeMetaToExchangeSummariesReaderMap.put(ExchangeMeta.POLONIEX, poloniexExchangeReader);
		exchangeMetaToExchangeSummariesReaderMap.put(ExchangeMeta.COINEXCHANGE, coinexchangeExchangeReader);
		exchangeMetaToExchangeSummariesReaderMap.put(ExchangeMeta.HITBTC, hitBtcExchangeReaderImpl);
		exchangeMetaToExchangeSummariesReaderMap.put(ExchangeMeta.KUCOIN, kucoinExchangeReaderImpl);

		return exchangeMetaToExchangeSummariesReaderMap;
	}
}
