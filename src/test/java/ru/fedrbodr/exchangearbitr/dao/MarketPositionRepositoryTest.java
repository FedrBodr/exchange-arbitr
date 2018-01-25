package ru.fedrbodr.exchangearbitr.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import ru.fedrbodr.exchangearbitr.model.MarketPosition;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class MarketPositionRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private MarketPositionRepository marketPositionRepository;

	@Test
	public void insertReadByNameTest() {
		// given
		MarketPosition btcLtc = new MarketPosition("BTC-LTC");
		entityManager.persist(btcLtc);
		entityManager.flush();

		// when
		MarketPosition btcLtcFound = marketPositionRepository.findByMarketName(btcLtc.getMarketName());

		// then
		assertEquals(btcLtcFound.getMarketName(), btcLtc.getMarketName());
	}

}