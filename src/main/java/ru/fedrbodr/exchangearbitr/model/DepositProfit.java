package ru.fedrbodr.exchangearbitr.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DepositProfit {
	private BigDecimal deposit;
	private BigDecimal averageSellStackPrice;
	private BigDecimal averageBuyStackPrice;
	private BigDecimal finalCoinsAmount;
	private BigDecimal profit;


}
