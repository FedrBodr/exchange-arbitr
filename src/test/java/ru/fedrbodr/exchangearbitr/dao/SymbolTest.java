package ru.fedrbodr.exchangearbitr.dao;

import org.junit.Test;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.Symbol;

import static org.junit.Assert.assertEquals;

public class SymbolTest {

	@Test
	public void equalsTest() {
		Symbol symbol = new Symbol("BTC-LTC","BTC", "LTC");
		symbol.setId(1);
		Symbol symbol2 = new Symbol("BTC-LTC","BTC", "LTC");
		symbol2.setId(1);

		assertEquals(symbol, symbol2);
		assertEquals(symbol2, symbol);
		assertEquals(symbol.hashCode(), symbol2.hashCode());

	}
}