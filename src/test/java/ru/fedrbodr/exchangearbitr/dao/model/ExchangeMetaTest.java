package ru.fedrbodr.exchangearbitr.dao.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ExchangeMetaTest {

	@Test
	public void equalsTest() {

		assertEquals(ExchangeMeta.BITTREX, ExchangeMeta.BITTREX);
		assertEquals(ExchangeMeta.BITTREX, ExchangeMeta.BITTREX);
		assertNotEquals(ExchangeMeta.BITTREX, ExchangeMeta.POLONIEX);

	}

}