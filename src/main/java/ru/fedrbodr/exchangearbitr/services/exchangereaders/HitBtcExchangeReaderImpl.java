package ru.fedrbodr.exchangearbitr.services.exchangereaders;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.hitbtc.v2.HitbtcAdapters;
import org.knowm.xchange.hitbtc.v2.dto.HitbtcCurrency;
import org.knowm.xchange.hitbtc.v2.dto.HitbtcTicker;
import org.knowm.xchange.hitbtc.v2.service.HitbtcMarketDataServiceRaw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.MarketPosition;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.Symbol;
import ru.fedrbodr.exchangearbitr.dao.shorttime.repo.MarketPositionFastRepository;
import ru.fedrbodr.exchangearbitr.dao.shorttime.repo.MarketPositionRepository;
import ru.fedrbodr.exchangearbitr.services.ExchangeReader;
import ru.fedrbodr.exchangearbitr.services.SymbolService;
import ru.fedrbodr.exchangearbitr.utils.MarketPosotionUtils;
import ru.fedrbodr.exchangearbitr.xchange.custom.ExchangeProxy;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

/**
 * Bittrex ExchangeMeta1 markect names  format now is main inner format ETH-BTC
 */
@Service
@Slf4j
public class HitBtcExchangeReaderImpl implements ExchangeReader {
	@Autowired
	private Map<ExchangeMeta, ExchangeProxy> exchangeMetaToExchangeProxyMap;
	@Autowired
	private MarketPositionFastRepository marketPositionFastRepository;
	@Autowired
	private MarketPositionRepository marketPositionRepository;
	@Autowired
	private SymbolService symbolService;
	private Map<String, HitbtcCurrency> hitBtcCurrencyMap;
	private ExchangeMeta exchangeMeta;
	private ExchangeProxy exchangeProxy;

	@PostConstruct
	private void init() throws InterruptedException {
		/*TODO refactor this with aop for all init methods*/
		log.info(HitBtcExchangeReaderImpl.class.getSimpleName() + " initialisation start");
		Date starDate = new Date();
		exchangeMeta = ExchangeMeta.HITBTC;
		hitBtcCurrencyMap = new HashMap<>();
		try {
			exchangeProxy = exchangeMetaToExchangeProxyMap.get(ExchangeMeta.HITBTC);
			List<CurrencyPair> exchangeSymbols = exchangeProxy.getNextExchange().getExchangeSymbols();
			List<HitbtcCurrency> hitbtcCurrencies = ((HitbtcMarketDataServiceRaw) exchangeProxy.getNextExchange().getMarketDataService()).getHitbtcCurrencies();
			for (HitbtcCurrency hitbtcCurrency : hitbtcCurrencies) {
				hitBtcCurrencyMap.put(hitbtcCurrency.getId(), hitbtcCurrency);
			}
			for (CurrencyPair exchangeSymbol : exchangeSymbols) {
				/* USE it when start working with fee
				 * CurrencyPairMetaData currencyPairMetaData = currencyPairs.get(currencyPair);
				 * currencyPairMetaData.getTradingFee()*/
				symbolService.getOrCreateNewSymbol(exchangeSymbol.counter.getCurrencyCode(), exchangeSymbol.base.getCurrencyCode());
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}

		/*TODO refactor this with aop*/
		log.info(HitBtcExchangeReaderImpl.class.getSimpleName() + " initialisation end, execution time: {}", new Date().getTime() - starDate.getTime());
	}

	public void readAndSaveMarketPositionsBySummaries() throws JSONException {
		Date starDate = new Date();
		/*TODO refactor this with aop for all init methods*/
		Map<String, HitbtcTicker> hitbtcTickers = new HashMap<>();
		try {
			hitbtcTickers = ((HitbtcMarketDataServiceRaw) exchangeProxy.getNextExchange().getMarketDataService()).getHitbtcTickers();
		} catch (IOException e) {
			log.error("Error occurred while getHitbtcTickers ", e);
		}

		List<MarketPosition> marketPositionList = new ArrayList<>();
		for (String symbol : hitbtcTickers.keySet()) {
			HitbtcTicker hitbtcTicker = hitbtcTickers.get(symbol);
			CurrencyPair currencyPair = HitbtcAdapters.adaptSymbol(hitbtcTicker.getSymbol());
			Symbol uniSymbol = symbolService.getOrCreateNewSymbol(currencyPair.counter.getCurrencyCode(), currencyPair.base.getCurrencyCode());
			marketPositionList.add(new MarketPosition(exchangeMeta, uniSymbol,
					hitbtcTicker.getLast(), hitbtcTicker.getBid(), hitbtcTicker.getAsk(), isSymbolActive(uniSymbol))
			);
		}

		marketPositionFastRepository.save(MarketPosotionUtils.convertMarketPosotionListToFast(marketPositionList));
		marketPositionFastRepository.flush();
//		log.info("HitBtc readAndSaveMarketPositionsBySummaries end, execution time sec: {}", (new Date().getTime() - starDate.getTime()) / 1000);
	}

	private boolean isSymbolActive(Symbol uniSymbol) {
		String baseName = uniSymbol.getBaseName();
		String quoteName = uniSymbol.getQuoteName();
		if (!"USDT".equals(baseName) && hitBtcCurrencyMap.get(baseName) == null || hitBtcCurrencyMap.get(quoteName) == null) {
			return false;
		} else if ("USDT".equals(baseName)) {
			return hitBtcCurrencyMap.get(quoteName).getPayinEnabled() && hitBtcCurrencyMap.get(quoteName).getPayoutEnabled() && hitBtcCurrencyMap.get(quoteName).getTransferEnabled();
		}
		return hitBtcCurrencyMap.get(baseName).getPayinEnabled() && hitBtcCurrencyMap.get(baseName).getPayoutEnabled() && hitBtcCurrencyMap.get(baseName).getTransferEnabled() &&
				hitBtcCurrencyMap.get(quoteName).getPayinEnabled() && hitBtcCurrencyMap.get(quoteName).getPayoutEnabled() && hitBtcCurrencyMap.get(quoteName).getTransferEnabled();
	}
}
