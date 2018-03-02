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
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.Symbol;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.UniLimitOrder;
import ru.fedrbodr.exchangearbitr.dao.shorttime.repo.LimitOrderRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
public class LimitOrderRepositoryTest {
	public static final ExchangeMeta POLONIEX_EXCHANGE_META = ExchangeMeta.POLONIEX;
	@Autowired
	private TestEntityManager entityManager;
	@Autowired
	private LimitOrderRepository limitOrderRepository;
	private final Symbol symbol = new Symbol("BTC-DGB", "BTC", "DGB");

	@Before
	public void setup() {
		entityManager.persist(POLONIEX_EXCHANGE_META);
		entityManager.persist(symbol);
		entityManager.flush();
	}

	@After
	public void teardown() {

	}

	@Test
	public void findByExchangeMetaAndSymbol() {
		// given
		// final and original sum not calculated
		entityManager.persist(createLimitOrder((long) 1, 0.000081, 12.1, 0.1, 0.1, Order.OrderType.ASK));
		entityManager.persist(createLimitOrder((long) 2, 0.000082, 26.8, 0.1, 0.1, Order.OrderType.ASK));
		entityManager.persist(createLimitOrder((long) 3, 0.000083, 158.56, 0.1, 0.1, Order.OrderType.BID));
		entityManager.flush();
		// when
		List<UniLimitOrder> limitOrderList = limitOrderRepository.findFirst60ByExchangeMetaAndSymbolAndType(POLONIEX_EXCHANGE_META, symbol, Order.OrderType.ASK);
		// get second
		UniLimitOrder order = limitOrderList.get(1);
		// then
		assertEquals(new BigDecimal(0.000082), order.getLimitPrice());
		assertEquals(Order.OrderType.ASK, order.getType());
		assertEquals(3, limitOrderList.size());

		//limitOrderRepository.deleteByExchangeMetaAndSymbol(POLONIEX_EXCHANGE_META, symbol);
//		limitOrderList = limitOrderRepository.
//				findFirst60ByExchangeMetaAndSymbolAndType(POLONIEX_EXCHANGE_META, symbol, Order.OrderType.ASK);
//		assertEquals(0, limitOrderList.size());
		ExecutorService executor = Executors.newFixedThreadPool(16);

		Callable<Void> tCallable = () -> {
			try {
				limitOrderRepository.deleteByExchangeMetaAndSymbol(POLONIEX_EXCHANGE_META, symbol);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		};
		executor.submit(tCallable);
		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private UniLimitOrder createLimitOrder(Long id, double limitPrice, double originalAmount, double originalSum, double finalSum, Order.OrderType type) {
		UniLimitOrder order = new UniLimitOrder();

		order.setExchangeMeta(POLONIEX_EXCHANGE_META);
		order.setSymbol(symbol);
		order.setType(type);
		order.setLimitPrice(new BigDecimal(limitPrice));
		order.setOriginalAmount(new BigDecimal(originalAmount));
		order.setOriginalSum(new BigDecimal(originalSum));
		order.setFinalSum(new BigDecimal(finalSum));

		return order;
	}
}