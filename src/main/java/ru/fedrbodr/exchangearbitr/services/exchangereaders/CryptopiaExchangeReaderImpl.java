package ru.fedrbodr.exchangearbitr.services.exchangereaders;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.cryptopia.dto.marketdata.CryptopiaTicker;
import org.knowm.xchange.cryptopia.service.CryptopiaMarketDataServiceRaw;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.utils.jackson.CurrencyPairDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Cryptopia market summaries reader
 */
//@Service
@Slf4j
public class CryptopiaExchangeReaderImpl implements ExchangeReader {
	private ExchangeProxy exchangeProxy;
	@Autowired
	private Map<ExchangeMeta, ExchangeProxy> exchangeMetaToExchangeProxyMap;
	@Autowired
	private MarketPositionFastRepository marketPositionFastRepository;
	@Autowired
	private SymbolService symbolService;
	private ExchangeMeta exchangeMeta;

	@PostConstruct
	private void init() {
		/*TODO refactor this with aop for all init methods*/
		log.info(CryptopiaExchangeReaderImpl.class.getSimpleName() + " initialisation start");
		Date starDate = new Date();
		exchangeMeta = ExchangeMeta.CRYPTOPIA;
		exchangeProxy = exchangeMetaToExchangeProxyMap.get(exchangeMeta);

		List<CurrencyPair> exchangeSymbols = exchangeProxy.getNextExchange().getExchangeSymbols();
		for (CurrencyPair exchangeSymbol : exchangeSymbols) {
			/* USE it when start working with fee
			 CurrencyPairMetaData currencyPairMetaData = currencyPairs.get(currencyPair);
			 * currencyPairMetaData.getTradingFee()*/
			symbolService.getOrCreateNewSymbol(exchangeSymbol.counter.getCurrencyCode(), exchangeSymbol.base.getCurrencyCode());
		}

		/*TODO refactor this with aop*/
		log.info(CryptopiaExchangeReaderImpl.class.getSimpleName() + " initialisation end, execution time: {}", new Date().getTime() - starDate.getTime());
	}

	public void readAndSaveMarketPositionsBySummaries() throws JSONException {
		List<CryptopiaTicker> tikers = null;
		CryptopiaMarketDataServiceRaw nextExchange = (CryptopiaMarketDataServiceRaw) exchangeProxy.getNextExchange().getMarketDataService();
		try {
			tikers = nextExchange.getCryptopiaMarkets();
		} catch (IOException e) {
			log.error("Error occurred while getKucoinTickers on proxy " + ((Exchange )nextExchange).getExchangeSpecification().getProxyHost(), e);
		}

		List<MarketPosition> marketPositionList = new ArrayList<>();
		for (CryptopiaTicker tiker : tikers) {
			CurrencyPair currencyPair = CurrencyPairDeserializer.getCurrencyPairFromString(tiker.getLabel());
			Symbol uniSymbol = symbolService.getOrCreateNewSymbol(currencyPair.counter.getCurrencyCode(), currencyPair.base.getCurrencyCode());
			marketPositionList.add(new MarketPosition(exchangeMeta, uniSymbol, tiker.getLast(), tiker.getBid(), tiker.getAsk(), true)
			);
		}

		marketPositionFastRepository.save(MarketPosotionUtils.convertMarketPosotionListToFast(marketPositionList));
		marketPositionFastRepository.flush();
	}

}
