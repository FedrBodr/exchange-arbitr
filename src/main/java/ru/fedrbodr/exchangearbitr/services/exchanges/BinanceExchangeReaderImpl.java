package ru.fedrbodr.exchangearbitr.services.exchanges;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.binance.dto.marketdata.BinanceTicker24h;
import org.knowm.xchange.binance.dto.meta.exchangeinfo.BinanceExchangeInfo;
import org.knowm.xchange.binance.dto.meta.exchangeinfo.Symbol;
import org.knowm.xchange.binance.service.BinanceMarketDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.dao.MarketPositionFastRepository;
import ru.fedrbodr.exchangearbitr.dao.MarketPositionRepository;
import ru.fedrbodr.exchangearbitr.model.dao.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.model.dao.MarketPosition;
import ru.fedrbodr.exchangearbitr.model.dao.UniSymbol;
import ru.fedrbodr.exchangearbitr.services.ExchangeReader;
import ru.fedrbodr.exchangearbitr.services.MarketSummaryService;
import ru.fedrbodr.exchangearbitr.utils.MarketPosotionUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/**
 * Bittrex ExchangeMeta markect names  format now is main inner format ETH-BTC
 * */
@Service
@Slf4j
public class BinanceExchangeReaderImpl implements ExchangeReader {
	private final BinanceMarketDataService marketDataService = (BinanceMarketDataService) ExchangeFactory.INSTANCE.createExchange(BinanceExchange.class.getName()).getMarketDataService();

	@Autowired
	private MarketPositionRepository marketPositionRepository;
	@Autowired
	private MarketPositionFastRepository marketPositionFastRepository;
	@Autowired
	private MarketSummaryService marketSummaryService;

	private Map<String, UniSymbol> binanceSymbolToUniSymbolMap;

	@PostConstruct
	private void init() throws IOException {
		/*TODO refactor this with aop for all init methods*/
		log.info(BinanceExchangeReaderImpl.class.getSimpleName() + " initialisation start");
		Date starDate = new Date();
		binanceSymbolToUniSymbolMap = new HashMap<>();
		BinanceExchangeInfo exchangeInfo = marketDataService.getExchangeInfo();
		Symbol[] symbols = exchangeInfo.getSymbols();
		for (Symbol symbol : symbols) {
			UniSymbol uniSymbol = marketSummaryService.getOrCreateNewSymbol(
					symbol.getQuoteAsset() + "-" + symbol.getBaseAsset(),
					symbol.getBaseAsset(),
					symbol.getQuoteAsset());
			binanceSymbolToUniSymbolMap.put(symbol.getSymbol(), uniSymbol);
		}
		/*TODO refactor this with aop*/
		log.info(BinanceExchangeReaderImpl.class.getSimpleName() + " initialisation end, execution time: {}", new Date().getTime() - starDate.getTime());
	}

	public void readAndSaveMarketPositionsBySummaries() throws IOException, JSONException, ParseException {
		List<BinanceTicker24h> binanceTicker24hList = marketDataService.ticker24h();
		List<MarketPosition> marketPositionList = new ArrayList<>();
		for (BinanceTicker24h binanceTicker24h : binanceTicker24hList) {
			UniSymbol uniSymbol = binanceSymbolToUniSymbolMap.get(binanceTicker24h.getSymbol());
			MarketPosition marketPosition = new MarketPosition(ExchangeMeta.BINANCE, uniSymbol, binanceTicker24h.getLastPrice());
			marketPositionList.add(marketPosition);
		}

		marketPositionFastRepository.save(MarketPosotionUtils.convertMarketPosotionListToFast(marketPositionList));
		marketPositionFastRepository.flush();
		marketPositionRepository.save(marketPositionList);
		marketPositionRepository.flush();
	}
}
