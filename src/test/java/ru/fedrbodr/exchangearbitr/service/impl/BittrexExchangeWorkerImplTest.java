package ru.fedrbodr.exchangearbitr.service.impl;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;
import ru.fedrbodr.exchangearbitr.dao.MarketPositionRepository;
import ru.fedrbodr.exchangearbitr.service.ExchangeWorker;

import java.io.IOException;

@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest
@ComponentScan("ru.fedrbodr.exchangearbitr")
public class BittrexExchangeWorkerImplTest {
	@Autowired
	private MarketPositionRepository marketPositionRepository;

	@Autowired
	private ExchangeWorker exchangeReader;

	@Test
	public void insertReadByNameTest() throws IOException, JSONException {
		/*// when
		exchangeReader.readAndSaveMarketPositions();
		// and found
		MarketPosition btcLtcFound = marketPositionRepository.;
		// then
		assertEquals(btcLtcFound.getMarketName(), "BTC-LTC");
		assertEquals(btcLtcFound.getPrimaryCurrencyName(), "BTC");
		assertEquals(btcLtcFound.getSecondaryCurrencyName(), "LTC");*/
	}
}