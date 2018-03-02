package ru.fedrbodr.exchangearbitr.xchange.custom;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.service.marketdata.MarketDataService;

import java.util.ArrayList;
import java.util.List;


public class ExchangeProxy {
	public static final String COINEXCHANGE = "COINEXCHANGE";

	private List<Exchange> exchangePoll = new ArrayList<>();
	private List<MarketDataService> marketDataServicePoll = new ArrayList<>();
	private Integer lastExchangeProsyUsedNum = 0;
	private int lastMarketDataServiceProsyUsedNum = 0;

	public ExchangeProxy(String exchangeClassName) {
		exchangePoll.add(ExchangeFactory.INSTANCE.createExchange(exchangeClassName));
	}

	public ExchangeProxy(List<String> proxyHostPortList, String exchangeClassName) {
		try {
			for (String proxyHostAndPort : proxyHostPortList) {
				String[] split = proxyHostAndPort.split(":");
				exchangePoll.add(getExchangeProxy(split[0], Integer.parseInt(split[1]), exchangeClassName));
			}	
		}catch (Exception e){
			throw new IllegalArgumentException("Proxy list in incorrect format! Must be proxy.list: 185.128.215.224:8000,193.93.60.95:8000,193.93.60.236:8000");
		}
	}

	public ExchangeProxy(CoinexchangeMarketDataService marketDataService) {
		marketDataServicePoll.add(marketDataService);
	}

	public MarketDataService getNextMarketDataService(){
		MarketDataService nextExchangeProxyForUse = marketDataServicePoll.get(0);

		return nextExchangeProxyForUse;
	}

	public Exchange getNextExchange(){
		Exchange nextExchangeProxyForUse = null;
		synchronized (lastExchangeProsyUsedNum){
			nextExchangeProxyForUse = exchangePoll.get(lastExchangeProsyUsedNum);
			if(lastExchangeProsyUsedNum < exchangePoll.size()-1){
				lastExchangeProsyUsedNum++;
			}else{
				lastExchangeProsyUsedNum = 0;
			}
		}
		return nextExchangeProxyForUse;
	}


	private Exchange getExchangeProxy(String proxyHost, int proxyPort, String exchangeClassName) {
		Exchange exchangeViaProxy = ExchangeFactory.INSTANCE.createExchange(exchangeClassName);
		ExchangeSpecification exchangeSpec = exchangeViaProxy.getDefaultExchangeSpecification();
		exchangeSpec.setProxyHost(proxyHost);
		exchangeSpec.setProxyPort(proxyPort);
		exchangeViaProxy.applySpecification(exchangeSpec);
		return exchangeViaProxy;
	}
}
