package ru.fedrbodr.exchangearbitr.services.exchangereaders;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.kucoin.KucoinExchange;
import org.knowm.xchange.kucoin.dto.KucoinAdapters;
import org.knowm.xchange.kucoin.dto.KucoinResponse;
import org.knowm.xchange.kucoin.dto.marketdata.KucoinTicker;
import org.knowm.xchange.kucoin.service.KucoinMarketDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.dao.MarketPositionFastRepository;
import ru.fedrbodr.exchangearbitr.dao.model.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.dao.model.MarketPosition;
import ru.fedrbodr.exchangearbitr.dao.model.SymbolPair;
import ru.fedrbodr.exchangearbitr.services.ExchangeReader;
import ru.fedrbodr.exchangearbitr.services.SymbolService;
import ru.fedrbodr.exchangearbitr.utils.MarketPosotionUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Bittrex ExchangeMeta markect names  format now is main inner format ETH-BTC
 */
@Service
@Slf4j
public class KucoinExchangeReaderImpl implements ExchangeReader {
	private KucoinExchange exchange;
	@Autowired
	private Map<ExchangeMeta, Exchange> exchangeMetaToExchangeMap;
	@Autowired
	private MarketPositionFastRepository marketPositionFastRepository;
	@Autowired
	private SymbolService symbolService;
	private KucoinMarketDataService marketDataService;
	private ExchangeMeta exchangeMeta;

	@PostConstruct
	private void init() {
		/*TODO refactor this with aop for all init methods*/
		log.info(KucoinExchangeReaderImpl.class.getSimpleName() + " initialisation start");
		Date starDate = new Date();
		exchangeMeta = ExchangeMeta.KUCOIN;

		exchange = (KucoinExchange) exchangeMetaToExchangeMap.get(exchangeMeta);
		marketDataService = (KucoinMarketDataService) exchange.getMarketDataService();

		List<CurrencyPair> exchangeSymbols = exchange.getExchangeSymbols();
		for (CurrencyPair exchangeSymbol : exchangeSymbols) {
			/* USE it when start working with fee
			 * CurrencyPairMetaData currencyPairMetaData = currencyPairs.get(currencyPair);
			 * currencyPairMetaData.getTradingFee()*/
			symbolService.getOrCreateNewSymbol(exchangeSymbol.counter.getSymbol(), exchangeSymbol.base.getSymbol());
		}

		/*TODO refactor this with aop*/
		log.info(KucoinExchangeReaderImpl.class.getSimpleName() + " initialisation end, execution time: {}", new Date().getTime() - starDate.getTime());
	}

	public void readAndSaveMarketPositionsBySummaries() throws JSONException {
		/*TODO refactor this with aop for all init methods*/
		KucoinResponse<List<KucoinTicker>> kucoinTickerList = null;
		try {
			kucoinTickerList = marketDataService.getKucoinTickers();
		} catch (IOException e) {
			log.error("Error occurred while getHitbtcTickers ", e);
		}
		List<KucoinTicker> tikers = kucoinTickerList.getData();
		List<MarketPosition> marketPositionList = new ArrayList<>();
		for (KucoinTicker tiker : tikers) {
			CurrencyPair currencyPair = KucoinAdapters.adaptSymbol(tiker.getSymbol());
			SymbolPair uniSymbol = symbolService.getOrCreateNewSymbol(currencyPair.counter.getCurrencyCode(), currencyPair.base.getSymbol());
			marketPositionList.add(new MarketPosition(exchangeMeta, uniSymbol,
					tiker.getLastDealPrice(), tiker.getBuy(), tiker.getSell(), tiker.getTrading())

			);
		}

		marketPositionFastRepository.save(MarketPosotionUtils.convertMarketPosotionListToFast(marketPositionList));
		marketPositionFastRepository.flush();

	}

}
