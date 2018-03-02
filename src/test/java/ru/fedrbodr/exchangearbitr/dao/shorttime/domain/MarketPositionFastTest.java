package ru.fedrbodr.exchangearbitr.dao.shorttime.domain;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class MarketPositionFastTest {

	@Test
	public void equalsTest() {
		Symbol symbol = new Symbol("BTC-LTC","BTC", "LTC");
		symbol.setId(1);
		Symbol symbol2 = new Symbol("BTC-LTC","BTC", "LTC");
		symbol2.setId(1);

		MarketPositionFast marketPositionFast = getMarketPosition(symbol);
		MarketPositionFast marketPositionFast2 = getMarketPosition(symbol);
		boolean equals = marketPositionFast.equals(marketPositionFast2);
		assertEquals(marketPositionFast, marketPositionFast2);

		Set<MarketPositionFast> marketPositionFastSet = new HashSet<>();
		marketPositionFastSet.add(marketPositionFast);
		marketPositionFastSet.add(marketPositionFast2);

		assertEquals(1, marketPositionFastSet.size());
	}

	public MarketPositionFast getMarketPosition(Symbol symbol) {
		MarketPositionFast marketPositionFast = new MarketPositionFast();
		ExchangeMeta exchangeMeta = ExchangeMeta.BITTREX;
		MarketPositionFastPK marketPositionFastPk = new MarketPositionFastPK(exchangeMeta, symbol);
		marketPositionFast.setMarketPositionFastPK(marketPositionFastPk);
		return marketPositionFast;
	}
}