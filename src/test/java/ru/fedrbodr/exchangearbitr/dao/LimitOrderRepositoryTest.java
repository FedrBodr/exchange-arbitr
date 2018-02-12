package ru.fedrbodr.exchangearbitr.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.knowm.xchange.dto.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import ru.fedrbodr.exchangearbitr.dao.model.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.dao.model.SymbolPair;
import ru.fedrbodr.exchangearbitr.dao.model.UniLimitOrder;
import ru.fedrbodr.exchangearbitr.dao.model.UniLimitOrderPK;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
public class LimitOrderRepositoryTest {
	public static final ExchangeMeta POLONIEX_EXCHANGE_META = ExchangeMeta.POLONIEX;
	@Autowired
	private TestEntityManager entityManager;
	@Autowired
	private LimitOrderRepository limitOrderRepository;
	private final SymbolPair symbolPair = new SymbolPair("BTC-DGB","BTC", "DGB");

	@Before
	public void setup() {
		entityManager.persist(POLONIEX_EXCHANGE_META);
		entityManager.persist(symbolPair);
		entityManager.flush();
	}
	@After
	public void teardown() {

	}

	@Test
	public void findByExchangeMetaAndSymbolPair() {
		// given
		// final and original sum not calculated
		entityManager.persist(createLimitOrder((long) 1, 0.000081, 12.1, 0.1, 0.1, Order.OrderType.ASK));
		entityManager.persist(createLimitOrder((long) 2, 0.000082, 26.8, 0.1, 0.1, Order.OrderType.ASK));
		entityManager.persist(createLimitOrder((long) 3, 0.000083, 158.56, 0.1, 0.1, Order.OrderType.BID));
		entityManager.flush();
		// when
		List<UniLimitOrder> limitOrderList = limitOrderRepository.
				findByUniLimitOrderPk_ExchangeMetaAndUniLimitOrderPk_SymbolPairAndUniLimitOrderPk_type(POLONIEX_EXCHANGE_META, symbolPair, Order.OrderType.ASK);
		// get second
		UniLimitOrder order = limitOrderList.get(1);
		// then
		assertEquals(new BigDecimal(0.000082), order.getLimitPrice());
		assertEquals(Order.OrderType.ASK, order.getUniLimitOrderPk().getType());
		assertEquals(2, limitOrderList.size());
	}

	private UniLimitOrder createLimitOrder(Long id, double limitPrice, double originalAmount, double originalSum, double finalSum, Order.OrderType type) {
		UniLimitOrder order = new UniLimitOrder();
		UniLimitOrderPK orderPk = new UniLimitOrderPK();
		orderPk.setId(id);
		orderPk.setExchangeMeta(POLONIEX_EXCHANGE_META);
		orderPk.setSymbolPair(symbolPair);
		orderPk.setType(type);
		order.setUniLimitOrderPk(orderPk);
		order.setLimitPrice(new BigDecimal(limitPrice));
		order.setOriginalAmount(new BigDecimal(originalAmount));
		order.setOriginalSum(new BigDecimal(originalSum));
		order.setFinalSum(new BigDecimal(finalSum));

		return order;
	}
}