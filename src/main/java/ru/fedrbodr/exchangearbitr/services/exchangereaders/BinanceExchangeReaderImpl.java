package ru.fedrbodr.exchangearbitr.services.exchangereaders;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.knowm.xchange.binance.dto.marketdata.BinanceTicker24h;
import org.knowm.xchange.binance.dto.meta.exchangeinfo.BinanceExchangeInfo;
import org.knowm.xchange.binance.service.BinanceMarketDataService;
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

import static ru.fedrbodr.exchangearbitr.utils.SymbolsNamesUtils.binanceToUniCurrencyName;

/**
 * Bittrex ExchangeMeta1 markect names  format now is main inner format ETH-BTC
 */
@Service
@Slf4j
public class BinanceExchangeReaderImpl implements ExchangeReader {
	@Autowired
	private Map<ExchangeMeta, ExchangeProxy> exchangeMetaToExchangeProxyMap;
	@Autowired
	private MarketPositionFastRepository marketPositionFastRepository;
	@Autowired
	private SymbolService symbolService;
	/**
	 * Used because in binance all symbols are concatenated without delimiters
	 */
	private Map<String, Symbol> binanceSymbolToUniSymbolMap;

	@PostConstruct
	private void init() throws IOException {
		/*TODO refactor this with aop for all init methods*/
		log.info(BinanceExchangeReaderImpl.class.getSimpleName() + " initialisation start");

		Date starDate = new Date();
		binanceSymbolToUniSymbolMap = new HashMap<>();
		BinanceExchangeInfo exchangeInfo = getMarketDataService().getExchangeInfo();
		org.knowm.xchange.binance.dto.meta.exchangeinfo.Symbol[] bynanceSymbols = exchangeInfo.getSymbols();
		for (org.knowm.xchange.binance.dto.meta.exchangeinfo.Symbol symbol : bynanceSymbols) {
			Symbol uniSymbol = symbolService.getOrCreateNewSymbol(
					binanceToUniCurrencyName(symbol.getQuoteAsset()),
					binanceToUniCurrencyName(symbol.getBaseAsset()));
			binanceSymbolToUniSymbolMap.put(symbol.getSymbol(), uniSymbol);
		}
		/*TODO refactor this with aop*/
		log.info(BinanceExchangeReaderImpl.class.getSimpleName() + " initialisation end, execution time: {}", new Date().getTime() - starDate.getTime());
	}

	public void readAndSaveMarketPositionsBySummaries() throws IOException, JSONException {
		/*TODO refactor this with aop for all init methods*/
		List<BinanceTicker24h> binanceTicker24hList = getMarketDataService().ticker24h();
		List<MarketPosition> marketPositionList = new ArrayList<>();
		for (BinanceTicker24h binanceTicker24h : binanceTicker24hList) {
			Symbol symbol = binanceSymbolToUniSymbolMap.get(binanceToUniCurrencyName(binanceTicker24h.getSymbol()));

			marketPositionList.add(
					new MarketPosition(ExchangeMeta.BINANCE, symbol,
							binanceTicker24h.getLastPrice(), binanceTicker24h.getBidPrice(), binanceTicker24h.getAskPrice(),
							true)
			);
		}
		marketPositionFastRepository.save(MarketPosotionUtils.convertMarketPosotionListToFast(marketPositionList));
		marketPositionFastRepository.flush();

	}

	private BinanceMarketDataService getMarketDataService() {
		return (BinanceMarketDataService) exchangeMetaToExchangeProxyMap.get(ExchangeMeta.BINANCE).getNextExchange().getMarketDataService();
	}
}
