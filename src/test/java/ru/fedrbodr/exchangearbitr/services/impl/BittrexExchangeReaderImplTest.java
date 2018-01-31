package ru.fedrbodr.exchangearbitr.services.impl;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;
import ru.fedrbodr.exchangearbitr.dao.MarketPositionRepository;

import java.io.IOException;

@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest
@ComponentScan("ru.fedrbodr.exchangearbitr")
public class BittrexExchangeReaderImplTest {
	@Autowired
	private MarketPositionRepository marketPositionRepository;


	@Test
	public void insertReadByNameTest() throws IOException, JSONException {
		/*// when
		exchangeReader.readAndSaveMarketPositionsBySummaries();
		// and found
		MarketPosition btcLtcFound = marketPositionRepository.;
		// then
		assertEquals(btcLtcFound.getName(), "BTC-LTC");
		assertEquals(btcLtcFound.getBaseName(), "BTC");
		assertEquals(btcLtcFound.getQuoteName(), "LTC");*/
	}
}