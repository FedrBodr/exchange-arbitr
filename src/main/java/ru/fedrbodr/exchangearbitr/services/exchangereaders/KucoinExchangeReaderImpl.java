package ru.fedrbodr.exchangearbitr.services.exchangereaders;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.kucoin.dto.KucoinAdapters;
import org.knowm.xchange.kucoin.dto.KucoinResponse;
import org.knowm.xchange.kucoin.dto.marketdata.KucoinTicker;
import org.knowm.xchange.kucoin.service.KucoinMarketDataService;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Kucoin market summaries reader
 */
@Service
@Slf4j
public class KucoinExchangeReaderImpl implements ExchangeReader {
	private ExchangeProxy exchangeProxy;
	@Autowired
	private Map<ExchangeMeta, ExchangeProxy> exchangeMetaToExchangeProxyMap;
	@Autowired
	private MarketPositionFastRepository marketPositionFastRepository;
	@Autowired
	private MarketPositionRepository marketPositionRepository;
	@Autowired
	private SymbolService symbolService;
	private ExchangeMeta exchangeMeta;

	@PostConstruct
	private void init() {
		/*TODO refactor this with aop for all init methods*/
		log.info(KucoinExchangeReaderImpl.class.getSimpleName() + " initialisation start");
		Date starDate = new Date();
		exchangeMeta = ExchangeMeta.KUCOIN;
		exchangeProxy = exchangeMetaToExchangeProxyMap.get(exchangeMeta);

		List<CurrencyPair> exchangeSymbols = exchangeProxy.getNextExchange().getExchangeSymbols();
		for (CurrencyPair exchangeSymbol : exchangeSymbols) {
			/* USE it when start working with fee
			 * CurrencyPairMetaData currencyPairMetaData = currencyPairs.get(currencyPair);
			 * currencyPairMetaData.getTradingFee()*/
			symbolService.getOrCreateNewSymbol(exchangeSymbol.counter.getCurrencyCode(), exchangeSymbol.base.getCurrencyCode());
		}

		/*TODO refactor this with aop*/
		log.info(KucoinExchangeReaderImpl.class.getSimpleName() + " initialisation end, execution time: {}", new Date().getTime() - starDate.getTime());
	}

	/* TODO check bid ask*/
	public void readAndSaveMarketPositionsBySummaries() throws JSONException {
		/*TODO refactor this with aop for all init methods*/
		KucoinResponse<List<KucoinTicker>> kucoinTickerList = null;
		KucoinMarketDataService nextExchange = (KucoinMarketDataService) exchangeProxy.getNextExchange().getMarketDataService();
		try {
			kucoinTickerList = nextExchange.getKucoinTickers();
		} catch (IOException e) {
			log.error("Error occurred while getKucoinTickers on proxy " + ((Exchange )nextExchange).getExchangeSpecification().getProxyHost(), e);
		}
		List<KucoinTicker> tikers = kucoinTickerList.getData();
		List<MarketPosition> marketPositionList = new ArrayList<>();
		for (KucoinTicker tiker : tikers) {
			CurrencyPair currencyPair = KucoinAdapters.adaptSymbol(tiker.getSymbol());
			Symbol uniSymbol = symbolService.getOrCreateNewSymbol(currencyPair.counter.getCurrencyCode(), currencyPair.base.getCurrencyCode());
			marketPositionList.add(new MarketPosition(exchangeMeta, uniSymbol,
					tiker.getLastDealPrice(), tiker.getBuy(), tiker.getSell(), tiker.getTrading())

			);
		}

		marketPositionFastRepository.save(MarketPosotionUtils.convertMarketPosotionListToFast(marketPositionList));
		marketPositionFastRepository.flush();
	}

}
