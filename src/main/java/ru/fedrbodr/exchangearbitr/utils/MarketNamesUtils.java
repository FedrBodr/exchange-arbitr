package ru.fedrbodr.exchangearbitr.utils;

public class MarketNamesUtils {
	public static String convertPoloniexToUniversalMarketName(String poloniexMarketName) {
		return poloniexMarketName.replace("_","-");
	}

	public static String convertCoinexchangeToUniversalMarketName(String baseCurrencyCode, String marketAssetCode) {
		return baseCurrencyCode + "-" + marketAssetCode;
	}
}
