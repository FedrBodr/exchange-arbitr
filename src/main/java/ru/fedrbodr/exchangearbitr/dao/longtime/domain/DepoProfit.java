package ru.fedrbodr.exchangearbitr.dao.longtime.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.fedrbodr.exchangearbitr.model.DepositProfit;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Table(name = "depo_profit")
@Entity
@Data
@NoArgsConstructor
public class DepoProfit {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private BigDecimal deposit;
	/**
	 * average price on first exchange by sell orders for the deposit(we buy)
	 * */
	@Column(name = "average_buy_stack_price", precision = 19, scale = 9, nullable = false)
	private BigDecimal averageBuyStackPrice;
	/**
	 * average price on second exchange by buy orders for the deposit(we sell)
	 * */
	@Column(name = "average_sell_stack_price", precision = 19, scale = 9, nullable = false)
	private BigDecimal averageSellStackPrice;
	@Column(name = "final_coins_amount", precision = 19, scale = 9, nullable = false)
	private BigDecimal finalCoinsAmount;
	@Column(name = "profit", precision = 15, scale = 8)
	private BigDecimal profit;
	/** Limit price on first exchange by last sell order for the deposit(we buy) */
	@Column(name = "sell_limit_price", precision = 15, scale = 8)
	private BigDecimal sellLimitPrice;
	@Column(name = "sell_glass_updated")
	private Date sellGlassUpdated;
	/** Limit price on second exchange by last buy order for the deposit(we sell) */
	@Column(name = "buy_limit_price", precision = 15, scale = 8)
	private BigDecimal buyLimitPrice;
	@Column(name = "buy_glass_updated")
	private Date buyGlassUpdated;
	@ManyToOne
	@JoinColumn(name = "fork_id")
	private Fork fork;

	public DepoProfit(BigDecimal deposit, BigDecimal averageBuyStackPrice, BigDecimal averageSellStackPrice, BigDecimal finalCoinsAmount, BigDecimal profit) {
		this.deposit = deposit;
		this.averageSellStackPrice = averageSellStackPrice;
		this.averageBuyStackPrice = averageBuyStackPrice;
		this.finalCoinsAmount = finalCoinsAmount;
		this.profit = profit;
	}

	public DepoProfit(DepositProfit depositProfit) {
		this.deposit = depositProfit.getDeposit();
		this.averageBuyStackPrice = depositProfit.getAverageSellStackPrice();
		this.averageSellStackPrice = depositProfit.getAverageBuyStackPrice();
		this.finalCoinsAmount = depositProfit.getFinalCoinsAmount();
		this.profit = depositProfit.getProfit();
	}

	public static List<DepoProfit> convertToDepoProfitList(List<DepositProfit> depositProfits){
		List<DepoProfit> depoProfits = new ArrayList<>();
		for (DepositProfit depositProfit : depositProfits) {
			depoProfits.add(new DepoProfit(depositProfit));
		}
		return depoProfits;
	}
}
