package ru.fedrbodr.exchangearbitr.dao.shorttime.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.MarketOrder;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * Representing a limit order with multiple primary key (id, exchange_id, symbol_id) ordered by id
 * Also sum at base and quote symbols currencies according to id ordering
 * </p>
 * <p>
 * A limit order lets you set a minimum or maximum price before your trade will be treated by the exchange as a {@link MarketOrder}. There is no
 * guarantee that your conditions will be met on the exchange, so your order may not be executed.
 * </p>
 */
@Table(name = "uni_limit_order", indexes={@Index(name = "uni_limit_order_exchange_symbol_idx", columnList="exchange_id, symbol_id, type")})
@Entity
@Data
@NoArgsConstructor
public class UniLimitOrder implements Serializable {
	@Id
	@GeneratedValue
	private Long id;
	@Enumerated(EnumType.STRING)
	private Order.OrderType type;
	@ManyToOne
	@JoinColumn(name = "exchange_id")
	private ExchangeMeta exchangeMeta;
	@ManyToOne
	@JoinColumn(name = "symbol_id")
	private Symbol symbol;

	/**
	 * The limit price
	 */
	@Column(name = "limit_price", precision = 15, scale = 8)
	private BigDecimal limitPrice;
	@Column(name = "original_amount", precision = 19, scale = 9)
	private BigDecimal originalAmount;
	@Column(name = "original_sum", precision = 19, scale = 9)
	private BigDecimal originalSum;
	@Column(name = "final_sum", precision = 19, scale = 9)
	private BigDecimal finalSum;
	@Column(name = "time_stamp")
	private Date timeStamp;

	public UniLimitOrder(Order.OrderType type, ExchangeMeta exchangeMeta, Symbol symbol, BigDecimal limitPrice,
						 BigDecimal originalAmount, BigDecimal originalSum, BigDecimal finalSum, Date timeStamp) {
		this.type = type;
		this.exchangeMeta = exchangeMeta;
		this.symbol = symbol;
		this.limitPrice = limitPrice;
		this.originalAmount = originalAmount;
		this.originalSum = originalSum;
		this.finalSum = finalSum;
		this.timeStamp = timeStamp;
	}
}
