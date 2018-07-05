package ru.fedrbodr.exchangearbitr.services.exchangereaders;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.service.marketdata.MarketDataService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CoineggExchangeReaderImpl implements ExchangeReader {
	@Autowired
	private Map<ExchangeMeta, ExchangeProxy> exchangeMetaToExchangeProxyMap;
	@Autowired
	private MarketPositionFastRepository marketPositionFastRepository;
	@Autowired
	private SymbolService symbolService;

	@PostConstruct
	private void init() throws IOException {
		List<CurrencyPair> exchangeSymbols = exchangeMetaToExchangeProxyMap.get(ExchangeMeta.COINEGG).getNextExchange().getExchangeSymbols();
		for (CurrencyPair exchangeSymbol : exchangeSymbols) {

		}
	}

	public void readAndSaveMarketPositionsBySummaries() throws IOException, JSONException {
		List<CurrencyPair> exchangeSymbols = exchangeMetaToExchangeProxyMap.get(ExchangeMeta.COINEGG).getNextExchange().getExchangeSymbols();
		List<MarketPosition> marketPositionList = new ArrayList<>();

		for (CurrencyPair exchangeSymbol : exchangeSymbols) {
			Ticker ticker = getMarketDataService().getTicker(exchangeSymbol);
			Symbol symbol = symbolService.getOrCreateNewSymbol(exchangeSymbol.counter.getSymbol(), exchangeSymbol.base.getSymbol());
			marketPositionList.add(
					new MarketPosition(ExchangeMeta.BINANCE, symbol, ticker.getLast(), ticker.getBid(), ticker.getAsk(),true)
			);
		}

		marketPositionFastRepository.save(MarketPosotionUtils.convertMarketPosotionListToFast(marketPositionList));
		marketPositionFastRepository.flush();

	}

	private MarketDataService getMarketDataService() {
		return exchangeMetaToExchangeProxyMap.get(ExchangeMeta.COINEGG).getNextExchange().getMarketDataService();
	}
}
