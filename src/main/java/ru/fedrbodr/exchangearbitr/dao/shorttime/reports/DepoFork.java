package ru.fedrbodr.exchangearbitr.dao.shorttime.reports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
/**
 * Agregate entyty for reports
* */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepoFork {
	private BigDecimal deposit;
	/**
	 * Sell exchange where we buy
	 * */
	private int sellExchangeMetaId;
	private BigDecimal averageSellStackPrice;
	/**
	 * Buy exchange where we sell
	 * */
	private int buyExchangeMetaId;
	private BigDecimal averageBuyStackPrice;
	private BigDecimal finalCoinsAmount;
	private BigDecimal profit;
	private String symbolName;
	/** Limit price on first exchange by last sell order for the deposit(we buy) */
	private BigDecimal sellLimitPrice;
	/** Limit price on second exchange by last buy order for the deposit(we sell) */
	private BigDecimal buyLimitPrice;

}
