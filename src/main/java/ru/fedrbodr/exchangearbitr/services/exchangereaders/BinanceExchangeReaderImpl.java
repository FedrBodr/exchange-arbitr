package ru.fedrbodr.exchangearbitr.services.exchangereaders;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.binance.dto.marketdata.BinanceTicker24h;
import org.knowm.xchange.binance.dto.meta.exchangeinfo.BinanceExchangeInfo;
import org.knowm.xchange.binance.service.BinanceMarketDataService;
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
import java.text.ParseException;
import java.util.*;

import static ru.fedrbodr.exchangearbitr.utils.SymbolsNamesUtils.binanceToUniCurrencyName;

/**
 * Bittrex ExchangeMeta markect names  format now is main inner format ETH-BTC
 */
@Service
@Slf4j
public class BinanceExchangeReaderImpl implements ExchangeReader {
	private BinanceMarketDataService marketDataService;
	@Autowired
	private Map<ExchangeMeta, Exchange> exchangeMetaToExchangeMap;
	@Autowired
	private MarketPositionRepository marketPositionRepository;
	@Autowired
	private MarketPositionFastRepository marketPositionFastRepository;
	@Autowired
	private SymbolService symbolService;
	/**
	 * Used because in binance all symbols are concatenated without delimiters
	 */
	private Map<String, SymbolPair> binanceSymbolToUniSymbolMap;

	@PostConstruct
	private void init() throws IOException {
		/*TODO refactor this with aop for all init methods*/
		log.info(BinanceExchangeReaderImpl.class.getSimpleName() + " initialisation start");

		marketDataService = (BinanceMarketDataService) exchangeMetaToExchangeMap.get(ExchangeMeta.BINANCE).getMarketDataService();
		Date starDate = new Date();
		binanceSymbolToUniSymbolMap = new HashMap<>();
		BinanceExchangeInfo exchangeInfo = marketDataService.getExchangeInfo();
		org.knowm.xchange.binance.dto.meta.exchangeinfo.Symbol[] bynanceSymbols = exchangeInfo.getSymbols();
		for (org.knowm.xchange.binance.dto.meta.exchangeinfo.Symbol symbol : bynanceSymbols) {
			SymbolPair uniSymbolPair = symbolService.getOrCreateNewSymbol(
					binanceToUniCurrencyName(symbol.getQuoteAsset()),
					binanceToUniCurrencyName(symbol.getBaseAsset()));
			binanceSymbolToUniSymbolMap.put(symbol.getSymbol(), uniSymbolPair);
		}
		/*TODO refactor this with aop*/
		log.info(BinanceExchangeReaderImpl.class.getSimpleName() + " initialisation end, execution time: {}", new Date().getTime() - starDate.getTime());
	}

	public void readAndSaveMarketPositionsBySummaries() throws IOException, JSONException, ParseException {
		/*TODO refactor this with aop for all init methods*/
		List<BinanceTicker24h> binanceTicker24hList = marketDataService.ticker24h();
		List<MarketPosition> marketPositionList = new ArrayList<>();
		for (BinanceTicker24h binanceTicker24h : binanceTicker24hList) {
			SymbolPair symbolPair = binanceSymbolToUniSymbolMap.get(binanceToUniCurrencyName(binanceTicker24h.getSymbol()));

			marketPositionList.add(
					new MarketPosition(ExchangeMeta.BINANCE, symbolPair,
							binanceTicker24h.getLastPrice(), binanceTicker24h.getBidPrice(), binanceTicker24h.getAskPrice(),
							true)
			);
		}

		marketPositionFastRepository.save(MarketPosotionUtils.convertMarketPosotionListToFast(marketPositionList));
		marketPositionFastRepository.flush();

	}
}
