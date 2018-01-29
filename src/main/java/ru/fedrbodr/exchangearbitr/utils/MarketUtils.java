package ru.fedrbodr.exchangearbitr.utils;

public class MarketUtils {
	public static String convertPoloniexToUniversalMarketName(String poloniexMarketName) {
		return poloniexMarketName.replace("_","-");
	}
}
