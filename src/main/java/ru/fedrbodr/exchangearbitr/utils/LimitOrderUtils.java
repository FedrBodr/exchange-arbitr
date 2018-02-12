package ru.fedrbodr.exchangearbitr.utils;

import org.knowm.xchange.dto.trade.LimitOrder;
import ru.fedrbodr.exchangearbitr.dao.model.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.dao.model.SymbolPair;
import ru.fedrbodr.exchangearbitr.dao.model.UniLimitOrder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LimitOrderUtils {
	public static List<UniLimitOrder> convertToUniLimitOrderListWithCalcSums(List<LimitOrder> orders, ExchangeMeta exchangeMeta,
																			 SymbolPair symbolPair, Date orderReadingTimeStamp) {
		Long orderId = 1L;
		BigDecimal originalAmountSum = BigDecimal.ZERO;
		BigDecimal finalSum = BigDecimal.ZERO;
		List<UniLimitOrder> uniOrderList = new ArrayList<>();
		for (LimitOrder askOrBid : orders) {
			originalAmountSum = originalAmountSum.add(askOrBid.getOriginalAmount());
			finalSum = finalSum.add(askOrBid.getLimitPrice().multiply(askOrBid.getOriginalAmount()));
			UniLimitOrder order =
					new UniLimitOrder(askOrBid, orderId, exchangeMeta, symbolPair, orderReadingTimeStamp, originalAmountSum, finalSum);
			uniOrderList.add(order);
			orderId++;
		}
		return uniOrderList;
	}
}
