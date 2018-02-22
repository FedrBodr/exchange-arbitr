package ru.fedrbodr.exchangearbitr.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.commons.lang3.StringUtils;
import org.knowm.xchange.currency.CurrencyPair;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.MarketPositionFast;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.Symbol;

import java.security.InvalidParameterException;

import static org.knowm.xchange.currency.Currency.BCH;

@Slf4j
public class SymbolsNamesUtils {
	private static BidiMap binanceCurrencyNameToUniCurrencyName;

	static {
		binanceCurrencyNameToUniCurrencyName = new DualHashBidiMap();
		binanceCurrencyNameToUniCurrencyName.put("BCC", BCH.getCurrencyCode());
	}

	public static String convertUniversalToPoloniexSymbol(String uniSymbol) {
		return uniSymbol.replace("-", "_");
	}

	/**
	 * Be carreful it is used only for url by  sybol, in other api places Coinexchange sybol has only id - use CoinexchangeExchangeReaderImpl.coinexchangeIdToSymbol
	 *
	 * @param symbol
	 */
	public static String convertUniversalToCoinexchangeSymbolForUrl(Symbol symbol) {
		return symbol.getQuoteName() + "/" + symbol.getBaseName();
	}

	/**
	 * Be carreful it is used only for url by  sybol, in other api places Coinexchange sybol has only id - use CoinexchangeExchangeReaderImpl.coinexchangeIdToSymbol
	 *
	 * @param symbol
	 */
	/*TODO if more similar formats like replace("-","_") in Binance and in Poloniex then refactor to symbolUnderLineFormat and etc*/
	public static String convertUniversalToBinanceUrlSymbol(Symbol symbol) {
		return uniToBinanceCurrencyName(symbol.getQuoteName()) + "_" + uniToBinanceCurrencyName(symbol.getBaseName());
	}

	public static String determineUrlToSymbolMarket(MarketPositionFast marketPosition) {
		String urlToSymbolMarket = null;
		Symbol symbol = marketPosition.getMarketPositionFastPK().getSymbol();
		ExchangeMeta exchangeMeta = marketPosition.getMarketPositionFastPK().getExchangeMeta();
		if (exchangeMeta.equals(ExchangeMeta.BITTREX)) {
			urlToSymbolMarket = exchangeMeta.getSymbolMarketUrl() + symbol.getName();
		} else if (exchangeMeta.equals(ExchangeMeta.POLONIEX)) {
			urlToSymbolMarket = exchangeMeta.getSymbolMarketUrl() + convertUniversalToPoloniexSymbol(symbol.getName());
		} else if (exchangeMeta.equals(ExchangeMeta.COINEXCHANGE)) {
			urlToSymbolMarket = exchangeMeta.getSymbolMarketUrl() + convertUniversalToCoinexchangeSymbolForUrl(symbol);
		} else if (exchangeMeta.equals(ExchangeMeta.BINANCE)) {
			urlToSymbolMarket = exchangeMeta.getSymbolMarketUrl() + convertUniversalToBinanceUrlSymbol(symbol);
		} else if (exchangeMeta.equals(ExchangeMeta.HITBTC)) {
			urlToSymbolMarket = exchangeMeta.getSymbolMarketUrl() + convertUniversalToHitBtcUrlSymbol(symbol);
		} else if (exchangeMeta.equals(ExchangeMeta.KUCOIN)) {
			urlToSymbolMarket = exchangeMeta.getSymbolMarketUrl() + convertUniversalToKucoinUrlSymbol(symbol);
		}

		if (StringUtils.isEmpty(urlToSymbolMarket)) {
			log.error("Can not determine url to symbol market for exchange " + exchangeMeta.getExchangeName(),
					new InvalidParameterException());
		}
		return urlToSymbolMarket;
	}

	private static String convertUniversalToKucoinUrlSymbol(Symbol symbol) {
		return symbol.getQuoteName() + "-" + symbol.getBaseName();
	}

	private static String convertUniversalToHitBtcUrlSymbol(Symbol symbol) {
		return symbol.getQuoteName()+"-to-"+ symbol.getBaseName();
	}

	/*TODO maybe refactor it to CyrrencyMapper and autowire to each exchangeReader */
	public static String binanceToUniCurrencyName(String binanceCurrencyName) {
		return binanceCurrencyNameToUniCurrencyName.get(binanceCurrencyName) != null ?
				(String) binanceCurrencyNameToUniCurrencyName.get(binanceCurrencyName) :
				binanceCurrencyName;
	}

	public static String uniToBinanceCurrencyName(String uniCurrencyName) {
		return binanceCurrencyNameToUniCurrencyName.getKey(uniCurrencyName) != null ?
				(String) binanceCurrencyNameToUniCurrencyName.getKey(uniCurrencyName) :
				uniCurrencyName;
	}

	/*TODO maybe refactor it to CyrrencyMapper and autowire to each exchangeReader */
	public static String bittrexToUniCurrencyName(String binanceCurrencyName) {
		return binanceCurrencyNameToUniCurrencyName.get(binanceCurrencyName) != null ?
				(String) binanceCurrencyNameToUniCurrencyName.get(binanceCurrencyName) :
				binanceCurrencyName;
	}

	public static String uniToBittrexCurrencyName(String uniCurrencyName) {
		return binanceCurrencyNameToUniCurrencyName.getKey(uniCurrencyName) != null ?
				(String) binanceCurrencyNameToUniCurrencyName.getKey(uniCurrencyName) :
				uniCurrencyName;
	}

	public static CurrencyPair getCurrencyPair(String base, String quote) {
		/*TODO optimise it for use precreated currencies
		* I am don`t know why but it`s working only changed names*/
		return new CurrencyPair(quote, base);
	}
}
