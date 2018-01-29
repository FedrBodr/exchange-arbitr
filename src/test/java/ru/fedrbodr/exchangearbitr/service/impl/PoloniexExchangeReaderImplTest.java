package ru.fedrbodr.exchangearbitr.service.impl;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;
import ru.fedrbodr.exchangearbitr.dao.MarketSummaryRepository;
import ru.fedrbodr.exchangearbitr.model.MarketSummary;
import ru.fedrbodr.exchangearbitr.service.ExchangeReader;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest
@ComponentScan("ru.fedrbodr.exchangearbitr")
public class PoloniexExchangeReaderImplTest {
	@Autowired
	private MarketSummaryRepository marketSummaryRepository;

	@Autowired
	@Qualifier("poloniexExchangeReaderImpl")
	private ExchangeReader exchangeReader;

	@Test
	public void severalTest() throws IOException, JSONException {
		// when
		exchangeReader.readAndSaveMarketPositions();
		// and found
		MarketSummary marketSummary = marketSummaryRepository.findByName("BTC-LTC");
		assertNotNull(marketSummary);
		assertNull(marketSummaryRepository.findByName("BTC_LTC"));

	}
}