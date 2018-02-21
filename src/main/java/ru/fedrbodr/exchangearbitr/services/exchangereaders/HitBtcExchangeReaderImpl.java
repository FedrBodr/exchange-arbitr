package ru.fedrbodr.exchangearbitr.services.exchangereaders;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.hitbtc.v2.HitbtcAdapters;
import org.knowm.xchange.hitbtc.v2.HitbtcExchange;
import org.knowm.xchange.hitbtc.v2.dto.HitbtcCurrency;
import org.knowm.xchange.hitbtc.v2.dto.HitbtcTicker;
import org.knowm.xchange.hitbtc.v2.service.HitbtcMarketDataService;
import org.knowm.xchange.hitbtc.v2.service.HitbtcMarketDataServiceRaw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.dao.MarketPositionFastRepository;
import ru.fedrbodr.exchangearbitr.dao.MarketPositionRepository;
import ru.fedrbodr.exchangearbitr.dao.model.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.dao.model.MarketPosition;
import ru.fedrbodr.exchangearbitr.dao.model.SymbolPair;
import ru.fedrbodr.exchangearbitr.services.ExchangeReader;
import ru.fedrbodr.exchangearbitr.services.SymbolService;
import ru.fedrbodr.exchangearbitr.utils.MarketPosotionUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

/**
 * Bittrex ExchangeMeta markect names  format now is main inner format ETH-BTC
 */
@Service
@Slf4j
public class HitBtcExchangeReaderImpl implements ExchangeReader {
	private HitbtcExchange exchange;
	@Autowired
	private Map<ExchangeMeta, Exchange> exchangeMetaToExchangeMap;
	@Autowired
	private MarketPositionFastRepository marketPositionFastRepository;
	@Autowired
	private MarketPositionRepository marketPositionRepository;
	@Autowired
	private SymbolService symbolService;
	private Map<String, HitbtcCurrency> hitBtcCurrencyMap;
	private HitbtcMarketDataService marketDataService;
	private ExchangeMeta exchangeMeta;

	@PostConstruct
	private void init() {
		/*TODO refactor this with aop for all init methods*/
		log.info(HitBtcExchangeReaderImpl.class.getSimpleName() + " initialisation start");
		Date starDate = new Date();
		exchangeMeta = ExchangeMeta.HITBTC;
		hitBtcCurrencyMap = new HashMap<>();
		exchange = (HitbtcExchange) exchangeMetaToExchangeMap.get(exchangeMeta);
		marketDataService = (HitbtcMarketDataService) exchange.getMarketDataService();

		try {
			List<CurrencyPair> exchangeSymbols = exchange.getExchangeSymbols();
			List<HitbtcCurrency> hitbtcCurrencies = ((HitbtcMarketDataServiceRaw) exchange.getMarketDataService()).getHitbtcCurrencies();
			for (HitbtcCurrency hitbtcCurrency : hitbtcCurrencies) {
				hitBtcCurrencyMap.put(hitbtcCurrency.getId(), hitbtcCurrency);
			}
			for (CurrencyPair exchangeSymbol : exchangeSymbols) {
				/* USE it when start working with fee
				 * CurrencyPairMetaData currencyPairMetaData = currencyPairs.get(currencyPair);
				 * currencyPairMetaData.getTradingFee()*/
				symbolService.getOrCreateNewSymbol(exchangeSymbol.counter.getSymbol(), exchangeSymbol.base.getSymbol());
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}

		/*TODO refactor this with aop*/
		log.info(HitBtcExchangeReaderImpl.class.getSimpleName() + " initialisation end, execution time: {}", new Date().getTime() - starDate.getTime());
	}

	public void readAndSaveMarketPositionsBySummaries() throws JSONException {
		/*TODO refactor this with aop for all init methods*/
		Map<String, HitbtcTicker> hitbtcTickers = new HashMap<>();
		try {
			hitbtcTickers = marketDataService.getHitbtcTickers();
		} catch (IOException e) {
			log.error("Error occurred while getHitbtcTickers ", e);
		}

		List<MarketPosition> marketPositionList = new ArrayList<>();
		for (String symbol : hitbtcTickers.keySet()) {
			HitbtcTicker hitbtcTicker = hitbtcTickers.get(symbol);
			CurrencyPair currencyPair = HitbtcAdapters.adaptSymbol(hitbtcTicker.getSymbol());
			SymbolPair uniSymbol = symbolService.getOrCreateNewSymbol(currencyPair.counter.getCurrencyCode(), currencyPair.base.getSymbol());
			marketPositionList.add(new MarketPosition(exchangeMeta, uniSymbol,
					hitbtcTicker.getLast(), hitbtcTicker.getBid(), hitbtcTicker.getAsk(), isSymbolPairActive(uniSymbol))
			);
		}

		/*marketPositionRepository.save(marketPositionList);
		marketPositionRepository.flush();*/

		marketPositionFastRepository.save(MarketPosotionUtils.convertMarketPosotionListToFast(marketPositionList));
		marketPositionFastRepository.flush();
	}

	private boolean isSymbolPairActive(SymbolPair uniSymbol) {
		String baseName = uniSymbol.getBaseName();
		String quoteName = uniSymbol.getQuoteName();
		if(!"USDT".equals(baseName) && hitBtcCurrencyMap.get(baseName)==null || hitBtcCurrencyMap.get(quoteName)==null) {
			return false;
		}else if ("USDT".equals(baseName)){
			return hitBtcCurrencyMap.get(quoteName).getPayinEnabled() && hitBtcCurrencyMap.get(quoteName).getPayoutEnabled() && hitBtcCurrencyMap.get(quoteName).getTransferEnabled();
		}
		return hitBtcCurrencyMap.get(baseName).getPayinEnabled() && hitBtcCurrencyMap.get(baseName).getPayoutEnabled() && hitBtcCurrencyMap.get(baseName).getTransferEnabled() &&
		hitBtcCurrencyMap.get(quoteName).getPayinEnabled() && hitBtcCurrencyMap.get(quoteName).getPayoutEnabled() && hitBtcCurrencyMap.get(quoteName).getTransferEnabled();
	}
}
