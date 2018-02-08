package ru.fedrbodr.exchangearbitr.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.commons.lang3.StringUtils;
import ru.fedrbodr.exchangearbitr.model.dao.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.model.dao.MarketPositionFast;
import ru.fedrbodr.exchangearbitr.model.dao.UniSymbol;

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
		return uniSymbol.replace("-","_");
	}
	/**
	 * Be carreful it is used only for url by  sybol, in other api places Coinexchange sybol has only id - use CoinexchangeExchangeReaderImpl.coinexchangeIdToMarketSummaryMap
	 *
	 * @param uniSymbol*/
	public static String convertUniversalToCoinexchangeSymbolForUrl(UniSymbol uniSymbol) {
		return uniSymbol.getQuoteName()+"/"+uniSymbol.getBaseName();
	}
	/**
	 * Be carreful it is used only for url by  sybol, in other api places Coinexchange sybol has only id - use CoinexchangeExchangeReaderImpl.coinexchangeIdToMarketSummaryMap
	 *
	 * @param uniSymbol*/
	/*TODO if more similar formats like replace("-","_") in Binance and in Poloniex then refactor to symbolUnderLineFormat and etc*/
	public static String convertUniversalToBinanceUrlSymbol(UniSymbol uniSymbol) {
		return uniToBinanceCurrencyName(uniSymbol.getQuoteName()) + "_" + uniToBinanceCurrencyName(uniSymbol.getBaseName());
	}

	public static String determineUrlToSymbolMarket(MarketPositionFast marketPosition) {
		String urlToSymbolMarket = null;
		UniSymbol uniSymbol = marketPosition.getMarketPositionFastPK().getUniSymbol();
		ExchangeMeta exchangeMeta = marketPosition.getMarketPositionFastPK().getExchangeMeta();
		if(exchangeMeta.equals(ExchangeMeta.BITTREX)){
			urlToSymbolMarket = exchangeMeta.getSymbolMarketUrl() + uniSymbol.getName();
		}else if(exchangeMeta.equals(ExchangeMeta.POLONIEX)){
			urlToSymbolMarket = exchangeMeta.getSymbolMarketUrl() + convertUniversalToPoloniexSymbol(uniSymbol.getName());
		}else if(exchangeMeta.equals(ExchangeMeta.COINEXCHANGE)){
			urlToSymbolMarket = exchangeMeta.getSymbolMarketUrl() + convertUniversalToCoinexchangeSymbolForUrl(uniSymbol);
		}else if(exchangeMeta.equals(ExchangeMeta.BINANCE)){
			urlToSymbolMarket = exchangeMeta.getSymbolMarketUrl() + convertUniversalToBinanceUrlSymbol(uniSymbol);
		}

		if(StringUtils.isEmpty(urlToSymbolMarket)){
			log.error("Can not determine url to symbol market for exchange " + exchangeMeta.getExchangeName(),
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
}
