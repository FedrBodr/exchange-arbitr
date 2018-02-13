package ru.fedrbodr.exchangearbitr.services.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.knowm.xchange.dto.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;
import ru.fedrbodr.exchangearbitr.dao.LimitOrderRepository;
import ru.fedrbodr.exchangearbitr.dao.SymbolPairRepository;
import ru.fedrbodr.exchangearbitr.dao.model.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.dao.model.SymbolPair;
import ru.fedrbodr.exchangearbitr.dao.model.UniLimitOrder;

import java.util.List;

import static org.junit.Assert.assertEquals;

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
	private SymbolPairRepository symbolPairRepository;

	private final SymbolPair symbolPair = new SymbolPair("BTC-DGB","BTC", "DGB");

	@Before
	public void setup() {
	}
	@After
	public void teardown() {
	}

	@Test
	public void readConvertCalcAndSaveUniOrders() {
		SymbolPair symbolPairPersisted = symbolPairRepository.findByName(symbolPair.getName());
		limitOrderService.readConvertCalcAndSaveUniOrders(symbolPairPersisted, POLONIEX_EXCHANGE_META);
		List<UniLimitOrder> uniLimitOrderList = limitOrderRepository.
				findFirst30ByUniLimitOrderPk_ExchangeMetaAndUniLimitOrderPk_SymbolPairAndUniLimitOrderPk_type(
						POLONIEX_EXCHANGE_META, symbolPairPersisted, Order.OrderType.ASK);

		UniLimitOrder firstUniLimitOrder = uniLimitOrderList.get(0);
		UniLimitOrder seccondUniLimitOrder = uniLimitOrderList.get(1);
		UniLimitOrder thirdLimitOrder = uniLimitOrderList.get(2);

		assertEquals(POLONIEX_EXCHANGE_META, firstUniLimitOrder.getUniLimitOrderPk().getExchangeMeta());
		assertEquals(firstUniLimitOrder.getUniLimitOrderPk().getExchangeMeta(), seccondUniLimitOrder.getUniLimitOrderPk().getExchangeMeta());
		assertEquals(Order.OrderType.ASK, seccondUniLimitOrder.getUniLimitOrderPk().getType());
	}
}