package ru.fedrbodr.exchangearbitr.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import ru.fedrbodr.exchangearbitr.model.Exchange;
import ru.fedrbodr.exchangearbitr.model.MarketPosition;
import ru.fedrbodr.exchangearbitr.model.Symbol;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
public class MarketPositionRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private MarketPositionRepository marketPositionRepository;

	@Test
	public void insertReadTest() {
		MarketPosition marketPositionForPersist = new MarketPosition();
		Symbol symbol = new Symbol("ETH-LTC", "ETH", "LTC");
		marketPositionForPersist.setSymbol(symbol);
		marketPositionForPersist.setExchange(Exchange.BITTREX);
		marketPositionForPersist.setCreateTime(LocalDateTime.now());
		marketPositionForPersist.setPrice(0.0000018);
		entityManager.persistAndFlush(symbol);
		entityManager.persistAndFlush(marketPositionForPersist);
		// when
		MarketPosition marketPositionFromDb = marketPositionRepository.findOne(marketPositionForPersist.getId());

		// then
		assertEquals(marketPositionFromDb.getSymbol().getName(), marketPositionForPersist.getSymbol().getName());
	}

}