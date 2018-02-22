package ru.fedrbodr.exchangearbitr.dao.longtime.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Table()
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
	@Column(name = "average_sell_stack_price", nullable = false)
	private BigDecimal averageSellStackPrice;
	/**
	 * average price on seccond exchange by buy orders for the deposit(we sell)
	 * */
	private BigDecimal averageBuyStackPrice;
	private BigDecimal finalCoinsAmount;
	private BigDecimal profit;
}
