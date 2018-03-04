package ru.fedrbodr.exchangearbitr.dao.shorttime.reports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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

}
