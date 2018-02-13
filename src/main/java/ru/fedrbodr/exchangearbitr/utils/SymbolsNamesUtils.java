package ru.fedrbodr.exchangearbitr.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.commons.lang3.StringUtils;
import org.knowm.xchange.currency.CurrencyPair;
import ru.fedrbodr.exchangearbitr.dao.model.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.dao.model.MarketPositionFast;
import ru.fedrbodr.exchangearbitr.dao.model.SymbolPair;

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
	 * Be carreful it is used only for url by  sybol, in other api places Coinexchange sybol has only id - use CoinexchangeExchangeReaderImpl.coinexchangeIdToSymbolPair
	 *
	 * @param symbolPair
	 */
	public static String convertUniversalToCoinexchangeSymbolForUrl(SymbolPair symbolPair) {
		return symbolPair.getQuoteName() + "/" + symbolPair.getBaseName();
	}

	/**
	 * Be carreful it is used only for url by  sybol, in other api places Coinexchange sybol has only id - use CoinexchangeExchangeReaderImpl.coinexchangeIdToSymbolPair
	 *
	 * @param symbolPair
	 */
	/*TODO if more similar formats like replace("-","_") in Binance and in Poloniex then refactor to symbolUnderLineFormat and etc*/
	public static String convertUniversalToBinanceUrlSymbol(SymbolPair symbolPair) {
		return uniToBinanceCurrencyName(symbolPair.getQuoteName()) + "_" + uniToBinanceCurrencyName(symbolPair.getBaseName());
	}

	public static String determineUrlToSymbolMarket(MarketPositionFast marketPosition) {
		String urlToSymbolMarket = null;
		SymbolPair symbolPair = marketPosition.getMarketPositionFastPK().getSymbolPair();
		ExchangeMeta exchangeMeta = marketPosition.getMarketPositionFastPK().getExchangeMeta();
		if (exchangeMeta.equals(ExchangeMeta.BITTREX)) {
			urlToSymbolMarket = exchangeMeta.getSymbolMarketUrl() + symbolPair.getName();
		} else if (exchangeMeta.equals(ExchangeMeta.POLONIEX)) {
			urlToSymbolMarket = exchangeMeta.getSymbolMarketUrl() + convertUniversalToPoloniexSymbol(symbolPair.getName());
		} else if (exchangeMeta.equals(ExchangeMeta.COINEXCHANGE)) {
			urlToSymbolMarket = exchangeMeta.getSymbolMarketUrl() + convertUniversalToCoinexchangeSymbolForUrl(symbolPair);
		} else if (exchangeMeta.equals(ExchangeMeta.BINANCE)) {
			urlToSymbolMarket = exchangeMeta.getSymbolMarketUrl() + convertUniversalToBinanceUrlSymbol(symbolPair);
		}

		if (StringUtils.isEmpty(urlToSymbolMarket)) {
			log.error("Can not determine url to symbolPair market for exchange " + exchangeMeta.getExchangeName(),
					new InvalidParameterException());
		}
		return urlToSymbolMarket;
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
