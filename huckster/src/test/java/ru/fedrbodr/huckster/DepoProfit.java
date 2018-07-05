package ru.fedrbodr.huckster;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class DepoProfit {
	private BigDecimal deposit;
	/**
	 * average price on first exchange by sell orders for the deposit(we buy)
	 */
	private BigDecimal averageBuyStackPrice;
	/**
	 * average price on second exchange by buy orders for the deposit(we sell)
	 */
	private BigDecimal averageSellStackPrice;
	private BigDecimal finalCoinsAmount;
	private BigDecimal profit;
	/**
	 * Limit price on first exchange by last sell order for the deposit(we buy)
	 */
	private BigDecimal sellLimitPrice;
	private Date sellGlassUpdated;
	/**
	 * Limit price on second exchange by last buy order for the deposit(we sell)
	 */
	private BigDecimal buyLimitPrice;

	public DepoProfit() {
	}

	public DepoProfit(BigDecimal deposit, BigDecimal averageBuyStackPrice, BigDecimal averageSellStackPrice, BigDecimal finalCoinsAmount, BigDecimal profit) {
		this.deposit = deposit;
		this.averageSellStackPrice = averageSellStackPrice;
		this.averageBuyStackPrice = averageBuyStackPrice;
		this.finalCoinsAmount = finalCoinsAmount;
		this.profit = profit;
	}
}