package ru.fedrbodr.exchangearbitr.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import ru.fedrbodr.exchangearbitr.dao.model.ExchangeMeta;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ExchangeMetaRepositoryTest {
	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private ExchangeMetaRepository exchangeMetaRepository;

	@Test
	public void findByIdTest() {
		// given
		entityManager.persist(ExchangeMeta.BITTREX);
		entityManager.persist(ExchangeMeta.COINEXCHANGE);
		entityManager.persist(ExchangeMeta.POLONIEX);
		entityManager.persist(ExchangeMeta.BINANCE);
		entityManager.flush();
		// when
		ExchangeMeta exchangeMeta = exchangeMetaRepository.findById(ExchangeMeta.COINEXCHANGE.getId());

		// then
		assertEquals(ExchangeMeta.COINEXCHANGE.getExchangeName(), exchangeMeta.getExchangeName());
	}
}