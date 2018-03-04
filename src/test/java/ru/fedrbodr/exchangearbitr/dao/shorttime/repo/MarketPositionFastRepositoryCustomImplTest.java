package ru.fedrbodr.exchangearbitr.dao.shorttime.repo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;
import ru.fedrbodr.exchangearbitr.dao.shorttime.reports.DepoFork;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@DataJpaTest
public class MarketPositionFastRepositoryCustomImplTest {
	@Configuration
	@EnableCaching
	@ComponentScan("ru.fedrbodr.exchangearbitr")
	static class TestConfig {}

	@Autowired
	private MarketPositionFastRepositoryCustom marketPositionFastRepositoryCustom;

	@Test
	public void selectTopForksByDeposit() {

		List<DepoFork> depoForks = marketPositionFastRepositoryCustom.selectTopForksByDeposit(BigDecimal.valueOf(0.001));
		assertNotNull(depoForks);
	}
}