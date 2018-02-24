package ru.fedrbodr.exchangearbitr.dao.longtime.repo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.DepoProfit;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.ExchangeMetaLong;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.Fork;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.SymbolLong;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ForkRepositoryTest {
	public static final DateFormat DATE_FORMAT = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
	@Autowired
	private ForkRepository forkRepository;
	@Autowired
	private TestEntityManager entityManager;
	private SymbolLong symbol = new SymbolLong("BTC-EL", "BTC", "EL");
	private SymbolLong symbol2 = new SymbolLong("BTC-TOR", "BTC", "TOR");

	@Before
	public void setup() throws ParseException {
		symbol = new SymbolLong("BTC-EL", "BTC", "EL");
		symbol2 = new SymbolLong("BTC-TOR", "BTC", "TOR");
		entityManager.persist(symbol);
		entityManager.persist(symbol2);
		entityManager.persist(ExchangeMetaLong.BITTREX);
		entityManager.persist(ExchangeMetaLong.POLONIEX);
		entityManager.persist(getNewFork(symbol, "22.02.2018 19:52"));
		entityManager.persist(getNewFork(symbol2, "22.02.2018 19:52"));
		entityManager.flush();
	}

	@After
	public void teardown() {
	}

	@Test
	public void testSaveMany() throws ParseException {
		entityManager.persist(getNewFork(symbol, "22.02.2018 19:54"));
		SymbolLong symbolNew = new SymbolLong("BTC-ELF", "BTC", "ELF");
		forkRepository.saveAndFlush(getNewFork(symbolNew, "22.02.2018 19:56"));
		forkRepository.saveAndFlush(getNewFork(symbolNew, "22.02.2018 19:57"));
		forkRepository.saveAndFlush(getNewFork(symbol, "22.02.2018 19:56"));
		List<Fork> all = forkRepository.findAll();
		assertNotNull(all);
		assertTrue(all.size() == 6);
		Fork one = forkRepository.findOne((long) 1);
		assertTrue(1 == one.getId());
		assertTrue(symbol.equals(one.getSymbol()));
	}

	@Test
	public void testFindOne() {
		List<Fork> all = forkRepository.findAll();
		assertNotNull(all);
		assertTrue(all.size() == 2);
		Fork one = forkRepository.findOne((long) 1);
		assertTrue(1 == one.getId());
		assertTrue(symbol.equals(one.getSymbol()));
	}

	@Test
	public void findFirstByBuyExchangeMetaAndSellExchangeMetaAndSymbol() throws ParseException {
		entityManager.persist(getNewFork(symbol, "22.02.2018 19:54"));
		entityManager.persist(getNewFork(symbol2, "22.02.2018 19:54"));
		entityManager.persist(getNewFork(symbol, "22.02.2018 19:56"));
		entityManager.persist(getNewFork(symbol2, "22.02.2018 19:56"));
		entityManager.flush();

		Fork firstByBuyExchangeMetaAndSellExchangeMetaAndSymbol = forkRepository.findFirstByBuyExchangeMetaIdAndSellExchangeMetaIdAndSymbolIdOrderByIdDesc(
				ExchangeMetaLong.BITTREX.getId(), ExchangeMetaLong.POLONIEX.getId(), symbol.getId());

		assertEquals("22.02.18 19:56",DATE_FORMAT.format(firstByBuyExchangeMetaAndSellExchangeMetaAndSymbol.getTimestamp()));
	}


	public List<DepoProfit> getProfitList() {
		List<DepoProfit> profitList = new ArrayList<>();
		profitList.add(new DepoProfit(new BigDecimal(0.1), new BigDecimal(0.00045), new BigDecimal(0.00090), new BigDecimal(0.2), new BigDecimal(1)));
		profitList.add(new DepoProfit(new BigDecimal(0.5), new BigDecimal(0.00040), new BigDecimal(0.00060), new BigDecimal(0.75), new BigDecimal(0.5)));
		return profitList;
	}

	private Fork getNewFork(SymbolLong symbol, String timeStamp) throws ParseException {
		Fork fork = new Fork();
		fork.setBuyExchangeMeta(ExchangeMetaLong.BITTREX);
		fork.setSellExchangeMeta(ExchangeMetaLong.POLONIEX);
		fork.setSymbol(symbol);
		fork.setProfits(getProfitList());
		fork.setTimestamp(DATE_FORMAT.parse(timeStamp));
		return fork;
	}

}