package ru.fedrbodr.exchangearbitr.rest.dto;

import lombok.Data;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.DepoProfit;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class DepoProfitDto {
	private Long id;
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
	private Date buyGlassUpdated;


	public DepoProfitDto(DepoProfit depositProfit) {
		this.id = depositProfit.getId();
		this.deposit = depositProfit.getDeposit();
		this.averageBuyStackPrice = depositProfit.getAverageBuyStackPrice();
		this.averageSellStackPrice = depositProfit.getAverageSellStackPrice();
		this.finalCoinsAmount = depositProfit.getFinalCoinsAmount();
		this.profit = depositProfit.getProfit();
		this.sellLimitPrice = depositProfit.getSellLimitPrice();
		this.sellGlassUpdated = depositProfit.getSellGlassUpdated();
		this.buyLimitPrice = depositProfit.getBuyLimitPrice();
		this.buyGlassUpdated = depositProfit.getBuyGlassUpdated();
	}
}
