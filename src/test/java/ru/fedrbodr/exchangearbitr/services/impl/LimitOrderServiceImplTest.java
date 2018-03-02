package ru.fedrbodr.exchangearbitr.services.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.Symbol;
import ru.fedrbodr.exchangearbitr.dao.shorttime.repo.LimitOrderRepository;
import ru.fedrbodr.exchangearbitr.dao.shorttime.repo.SymbolRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
public class LimitOrderServiceImplTest {
	@Configuration
	@EnableCaching
	@ComponentScan("ru.fedrbodr.exchangearbitr")
	static class TestConfig {}

	public static final ExchangeMeta POLONIEX_EXCHANGE_META = ExchangeMeta.POLONIEX;
	@Autowired
	private TestEntityManager entityManager;
	@Autowired
	private LimitOrderServiceImpl limitOrderService;
	@Autowired
	private LimitOrderRepository limitOrderRepository;
	@Autowired
	private SymbolRepository symbolRepository;

	private final Symbol symbol = new Symbol("BTC-DGB","BTC", "DGB");

	@Before
	public void setup() {
	}
	@After
	public void teardown() {
	}

	@Test
	public void readConvertCalcAndSaveUniOrders() {
/*
		Symbol symbolPairPersisted = symbolRepository.findByName(symbol.getName());
		limitOrderService.readConvertCalcAndSaveUniOrders(symbolPairPersisted, POLONIEX_EXCHANGE_META, "91.134.135.168", 3128);
		List<UniLimitOrder> uniLimitOrderList = limitOrderRepository.
				findFirst60ByExchangeMetaAndSymbolAndType(
						POLONIEX_EXCHANGE_META, symbolPairPersisted, Order.OrderType.ASK);

		UniLimitOrder firstUniLimitOrder = uniLimitOrderList.get(0);
		UniLimitOrder seccondUniLimitOrder = uniLimitOrderList.get(1);
		UniLimitOrder thirdLimitOrder = uniLimitOrderList.get(2);

		assertEquals(POLONIEX_EXCHANGE_META, firstUniLimitOrder.getUniLimitOrderHistoryPk().getExchangeMeta());
		assertEquals(firstUniLimitOrder.getUniLimitOrderHistoryPk().getExchangeMeta(), seccondUniLimitOrder.getUniLimitOrderHistoryPk().getExchangeMeta());
		assertEquals(Order.OrderType.ASK, seccondUniLimitOrder.getUniLimitOrderHistoryPk().getType());
*/
	}
}