package ru.fedrbodr.exchangearbitr.utils;

import org.junit.Test;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.LimitOrder;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.Symbol;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.UniLimitOrder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class LimitOrderUtilsTest {

	@Test
	public void convertToUniLimitOrderListWithCalcSums() {
		// given
		List<LimitOrder> orders = createTestOrders();

		ExchangeMeta exchangeMeta= ExchangeMeta.POLONIEX;
		Symbol symbol = new Symbol("BTC-DGB","BTC", "DGB");
		Date orderReadingTimeStamp = new Date();
		// when
		List<UniLimitOrder> uniLimitOrders = LimitOrderUtils.convertToUniLimitOrderListWithCalcSums(orders, exchangeMeta, symbol, orderReadingTimeStamp, Order.OrderType.BID);
		// then
		assertEquals(4, uniLimitOrders.size());
		assertEquals(new Long(4), uniLimitOrders.get(3).getId());
		assertEquals(new BigDecimal(50), uniLimitOrders.get(1).getOriginalSum());
		assertEquals(Order.OrderType.BID, uniLimitOrders.get(1).getType());
		assertEquals(Order.OrderType.BID, uniLimitOrders.get(2).getType());
		assertEquals(new BigDecimal(75), uniLimitOrders.get(3).getOriginalSum());
	}

	private List<LimitOrder> createTestOrders() {
		/* If you change data then change test asserts above */
		List<LimitOrder> limitOrders = new ArrayList<>();
		limitOrders.add(new LimitOrder.Builder(Order.OrderType.BID, CurrencyPair.BTC_USD).limitPrice(new BigDecimal("5")).originalAmount(new BigDecimal(20)).build());
		limitOrders.add(new LimitOrder.Builder(Order.OrderType.BID, CurrencyPair.BTC_USD).limitPrice(new BigDecimal("4")).originalAmount(new BigDecimal(30)).build());
		limitOrders.add(new LimitOrder.Builder(Order.OrderType.BID, CurrencyPair.BTC_USD).limitPrice(new BigDecimal("2")).originalAmount(new BigDecimal(10)).build());
		limitOrders.add(new LimitOrder.Builder(Order.OrderType.BID, CurrencyPair.BTC_USD).limitPrice(new BigDecimal("3")).originalAmount(new BigDecimal(15)).build());

		return limitOrders;
	}
}