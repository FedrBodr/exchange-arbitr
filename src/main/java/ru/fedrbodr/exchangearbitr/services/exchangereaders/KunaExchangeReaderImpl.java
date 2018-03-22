package ru.fedrbodr.exchangearbitr.services.exchangereaders;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.kuna.dto.KunaTimeTicker;
import org.knowm.xchange.kuna.service.KunaMarketDataServiceRaw;
import org.knowm.xchange.kuna.util.KunaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.MarketPosition;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.Symbol;
import ru.fedrbodr.exchangearbitr.dao.shorttime.repo.MarketPositionFastRepository;
import ru.fedrbodr.exchangearbitr.services.ExchangeReader;
import ru.fedrbodr.exchangearbitr.services.SymbolService;
import ru.fedrbodr.exchangearbitr.utils.MarketPosotionUtils;
import ru.fedrbodr.exchangearbitr.xchange.custom.ExchangeProxy;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

/**
 * Kuna market summaries reader
 */
@Service
@Slf4j
public class KunaExchangeReaderImpl implements ExchangeReader {
	private ExchangeProxy exchangeProxy;
	@Autowired
	private Map<ExchangeMeta, ExchangeProxy> exchangeMetaToExchangeProxyMap;
	@Autowired
	private MarketPositionFastRepository marketPositionFastRepository;
	@Autowired
	private SymbolService symbolService;
	private ExchangeMeta exchangeMeta;

	/*is no realised service to get currencies and it wil get at work */
	@PostConstruct
	private void init() {
		/*TODO refactor this with aop for all init methods*/
		log.info(KunaExchangeReaderImpl.class.getSimpleName() + " initialisation start");
		Date starDate = new Date();
		exchangeMeta = ExchangeMeta.KUNA;
		/*List<CurrencyPair> exchangeSymbols = exchangeProxy.getNextExchange().getExchangeSymbols();
		for (CurrencyPair exchangeSymbol : exchangeSymbols) {
			*//* USE it when start working with fee
			 CurrencyPairMetaData currencyPairMetaData = currencyPairs.get(currencyPair);
			 * currencyPairMetaData.getTradingFee()*//*
			symbolService.getOrCreateNewSymbol(exchangeSymbol.base.getCurrencyCode(), exchangeSymbol.counter.getCurrencyCode());
		}*/
		/*TODO refactor this with aop*/
		log.info(KunaExchangeReaderImpl.class.getSimpleName() + " initialisation end, execution time: {}", new Date().getTime() - starDate.getTime());
	}

	public void readAndSaveMarketPositionsBySummaries() throws JSONException {
		/*TODO refactor this with aop for all init methods*/
		Map<String, KunaTimeTicker> tikers = new HashMap<>();
		KunaMarketDataServiceRaw nextExchange = (KunaMarketDataServiceRaw) exchangeProxy.getNextExchange().getMarketDataService();
		try {
			tikers = nextExchange.getKunaTickers();
		} catch (IOException e) {
			log.error("Error occurred while getKucoinTickers on proxy " + ((Exchange )nextExchange).getExchangeSpecification().getProxyHost(), e);
		}

		List<MarketPosition> marketPositionList = new ArrayList<>();
		for (String symbol : tikers.keySet()) {
			KunaTimeTicker kunaTimeTicker = tikers.get(symbol);
			CurrencyPair currencyPair = KunaUtils.toCurrencyPair(symbol);
			Symbol uniSymbol = symbolService.getOrCreateNewSymbol(currencyPair.counter.getCurrencyCode(), currencyPair.base.getCurrencyCode());
			marketPositionList.add(new MarketPosition(exchangeMeta, uniSymbol,
					kunaTimeTicker.getTicker().getLast(), kunaTimeTicker.getTicker().getBuy(), kunaTimeTicker.getTicker().getSell(), true) );
		}

		marketPositionFastRepository.save(MarketPosotionUtils.convertMarketPosotionListToFast(marketPositionList));
		marketPositionFastRepository.flush();
	}

}
