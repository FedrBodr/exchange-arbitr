package ru.fedrbodr.exchangearbitr.utils;

import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.LimitOrder;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.Symbol;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.UniLimitOrder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LimitOrderUtils {
	public static List<UniLimitOrder> convertToUniLimitOrderListWithCalcSums(List<LimitOrder> orders, ExchangeMeta exchangeMeta,
																			 Symbol symbol, Date orderReadingTimeStamp, Order.OrderType type) {
		Long orderId = 1L;
		BigDecimal originalAmountSum = BigDecimal.ZERO;
		BigDecimal finalSum = BigDecimal.ZERO;
		List<UniLimitOrder> uniOrderList = new ArrayList<>();
		for (LimitOrder askOrBid : orders) {
			originalAmountSum = originalAmountSum.add(askOrBid.getOriginalAmount());
			finalSum = finalSum.add(askOrBid.getLimitPrice().multiply(askOrBid.getOriginalAmount()));
			UniLimitOrder order =
					new UniLimitOrder(type, exchangeMeta, symbol, askOrBid.getLimitPrice(),
							askOrBid.getOriginalAmount(), originalAmountSum, finalSum , orderReadingTimeStamp);
			uniOrderList.add(order);
			orderId++;
		}
		return uniOrderList;
	}
}
