package ru.fedrbodr.exchangearbitr.config;

import lombok.extern.slf4j.Slf4j;
import org.h2.server.web.WebServlet;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.bittrex.BittrexExchange;
import org.knowm.xchange.hitbtc.v2.HitbtcExchange;
import org.knowm.xchange.kucoin.KucoinExchange;
import org.knowm.xchange.poloniex.PoloniexExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import ru.fedrbodr.exchangearbitr.dao.model.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.services.ExchangeReader;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
@EnableAsync
@Slf4j
public class VariousAppConfig {
	@PostConstruct
	public void init(){
		/*TimeZone.setDefault(TimeZone.getTimeZone("GMT+3"));*/

	}

	@Bean
	public ServletRegistrationBean h2servletRegistration(){
		ServletRegistrationBean registrationBean = new ServletRegistrationBean( new WebServlet());
		registrationBean.addUrlMappings("/console/*");
		return registrationBean;
	}

	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);
		executor.setMaxPoolSize(10);
		executor.setQueueCapacity(25);
		return executor;
	}

	@Bean
	public Map<ExchangeMeta, Exchange> exchangeMetaToExchangeMap() {
		Map<ExchangeMeta, Exchange> exchangeMetaToExchangeMap;
		log.info("Before start initMarketDataServices");

		Date start = new Date();
		exchangeMetaToExchangeMap = new HashMap<>();
		exchangeMetaToExchangeMap.put(
				ExchangeMeta.BITTREX, ExchangeFactory.INSTANCE.createExchange(BittrexExchange.class.getName()));
		exchangeMetaToExchangeMap.put(
				ExchangeMeta.BINANCE, ExchangeFactory.INSTANCE.createExchange(BinanceExchange.class.getName()));
		exchangeMetaToExchangeMap.put(
				ExchangeMeta.POLONIEX, ExchangeFactory.INSTANCE.createExchange(PoloniexExchange.class.getName()));
		exchangeMetaToExchangeMap.put(
				ExchangeMeta.HITBTC, ExchangeFactory.INSTANCE.createExchange(HitbtcExchange.class.getName()));
		exchangeMetaToExchangeMap.put(
				ExchangeMeta.KUCOIN, ExchangeFactory.INSTANCE.createExchange(KucoinExchange.class.getName()));

		/* TODO REALIZE IN XCHANGE Coinexchange Exchange */
		log.info("After stop initMarketDataServices. time in  seconds: {}", (start.getTime() - new Date().getTime()) / 1000);
		return exchangeMetaToExchangeMap;
	}

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
