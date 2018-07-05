package ru.fedrbodr.huckster;

import lombok.Data;
import org.apache.commons.lang3.math.NumberUtils;
import org.knowm.xchange.dto.Order;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class UniLimitOrder {
	private Long id;
	private Order.OrderType type;
	private BigDecimal limitPrice;
	private BigDecimal originalAmount;
	private BigDecimal originalSum;
	private BigDecimal finalSum;
	private Date timeStamp;

	public UniLimitOrder(Long id, Order.OrderType type, BigDecimal limitPrice,
						 BigDecimal originalAmount, BigDecimal originalSum, BigDecimal finalSum) {
		this.type = type;
		this.limitPrice = limitPrice;
		this.originalAmount = originalAmount;
		this.originalSum = originalSum;
		this.finalSum = finalSum;

	}
}
