package ru.fedrbodr.exchangearbitr.config;

import lombok.extern.slf4j.Slf4j;
import org.h2.server.web.WebServlet;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.bittrex.BittrexExchange;
import org.knowm.xchange.poloniex.PoloniexExchange;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import ru.fedrbodr.exchangearbitr.dao.model.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.xchange.custom.CoinexchangeMarketDataService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
@EnableAsync
@Slf4j
public class VariousAppConfig {
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

	@Autowired
	private CoinexchangeMarketDataService coinexchangeMarketDataService;

	@Bean
	public Map<Integer, MarketDataService> exchangeIdToMarketDataService() {
		Map<Integer, MarketDataService> exchangeIdToMarketDataService;
		log.info("Before start initMarketDataServices");

		Date start = new Date();
		exchangeIdToMarketDataService = new HashMap<>();
		exchangeIdToMarketDataService.put(
				ExchangeMeta.BITTREX.getId(), ExchangeFactory.INSTANCE.createExchange(BittrexExchange.class.getName()).getMarketDataService());
		exchangeIdToMarketDataService.put(
				ExchangeMeta.BINANCE.getId(), ExchangeFactory.INSTANCE.createExchange(BinanceExchange.class.getName()).getMarketDataService());
		exchangeIdToMarketDataService.put(
				ExchangeMeta.POLONIEX.getId(), ExchangeFactory.INSTANCE.createExchange(PoloniexExchange.class.getName()).getMarketDataService());
		exchangeIdToMarketDataService.put(
				ExchangeMeta.COINEXCHANGE.getId(), coinexchangeMarketDataService);
		/* TODO REALIZE IN XCHANGE Coinexchange Exchange */
		log.info("After stop initMarketDataServices. time in  seconds: {}", (start.getTime() - new Date().getTime()) / 1000);
		return exchangeIdToMarketDataService;
	}
}
