package ru.fedrbodr.exchangearbitr.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import ru.fedrbodr.exchangearbitr.model.dao.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.model.dao.MarketPositionFast;
import ru.fedrbodr.exchangearbitr.model.dao.UniSymbol;

import java.security.InvalidParameterException;

@Slf4j
public class SymbolsNamesUtils {
	public static String convertPoloniexToUniversalSymbol(String poloniexSymbol) {
		return poloniexSymbol.replace("_","-");
	}
	public static String convertUniversalToPoloniexSymbol(String uniSymbol) {
		return uniSymbol.replace("-","_");
	}
	public static String convertCoinexchangeToUniversalSymbol(String baseCurrencyCode, String marketAssetCode) {
		return baseCurrencyCode + "-" + marketAssetCode;
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
	 * */
	/*TODO if more similar formats like replace("-","_") in Binance and in Poloniex then refactor to symbolUnderLineFormat and etc*/
	public static String convertUniversalToBinanceUrlSymbol(String uniSymbol) {
		return uniSymbol.replace("-","_");
	}

	public static String determineUrlToSymbolMarket(MarketPositionFast marketPosition) {
		String urlToSymbolMarket = null;
		UniSymbol uniSymbol = marketPosition.getMarketPositionFastPK().getUniSymbol();
		ExchangeMeta exchangeMeta = marketPosition.getMarketPositionFastPK().getExchangeMeta();
		if(exchangeMeta.equals(ExchangeMeta.BITTREX)){
			urlToSymbolMarket = exchangeMeta.getSymbolMarketUrl()+uniSymbol.getName();
		}else if(exchangeMeta.equals(ExchangeMeta.POLONIEX)){
			urlToSymbolMarket = exchangeMeta.getSymbolMarketUrl()+convertUniversalToPoloniexSymbol(uniSymbol.getName());
		}else if(exchangeMeta.equals(ExchangeMeta.COINEXCHANGE)){
			urlToSymbolMarket = exchangeMeta.getSymbolMarketUrl()+ convertUniversalToCoinexchangeSymbolForUrl(uniSymbol);
		}else if(exchangeMeta.equals(ExchangeMeta.BINANCE)){
			urlToSymbolMarket = exchangeMeta.getSymbolMarketUrl()+ convertUniversalToBinanceUrlSymbol(uniSymbol.getName());
		}

		if(StringUtils.isEmpty(urlToSymbolMarket)){
			log.error("Can not determine url to symbol market for exchange " + exchangeMeta.getExchangeName(),
					new InvalidParameterException());
		}
		return urlToSymbolMarket;
	}
}
