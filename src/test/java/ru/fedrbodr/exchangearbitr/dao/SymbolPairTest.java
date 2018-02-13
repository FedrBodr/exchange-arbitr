package ru.fedrbodr.exchangearbitr.dao;

import org.junit.Test;
import ru.fedrbodr.exchangearbitr.dao.model.SymbolPair;

import static org.junit.Assert.assertEquals;

public class SymbolPairTest {

	@Test
	public void equalsTest() {
		SymbolPair symbolPair = new SymbolPair("BTC-LTC","BTC", "LTC");
		symbolPair.setId(1);
		SymbolPair symbolPair2 = new SymbolPair("BTC-LTC","BTC", "LTC");
		symbolPair2.setId(1);

		assertEquals(symbolPair, symbolPair2);
		assertEquals(symbolPair2, symbolPair);
		assertEquals(symbolPair.hashCode(), symbolPair2.hashCode());

	}
}