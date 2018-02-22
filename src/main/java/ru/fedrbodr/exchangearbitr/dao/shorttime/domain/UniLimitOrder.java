package ru.fedrbodr.exchangearbitr.dao.shorttime.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
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
@Table
@Entity
@Data
@NoArgsConstructor
public class UniLimitOrder implements Serializable {
	@EmbeddedId
	private UniLimitOrderPK uniLimitOrderPk;
	/**
	 * The limit price
	 */
	@Column(name = "limit_price", precision = 15, scale = 8)
	private BigDecimal limitPrice;
	@Column(name = "original_price", precision = 17, scale = 8)
	private BigDecimal originalAmount;
	@Column(name = "time_stamp")
	private Date timeStamp;
	@Column(name = "original_sum", precision = 18, scale = 8)
	private BigDecimal originalSum;
	@Column(name = "final_sum", precision = 15, scale = 8)
	private BigDecimal finalSum;

	public UniLimitOrder(LimitOrder ask, Long id, ExchangeMeta exchangeMeta, Symbol symbol, Date timeStamp, BigDecimal originalSum, BigDecimal finalSum) {
		this.uniLimitOrderPk = new UniLimitOrderPK(id, exchangeMeta, symbol, ask.getType());
		this.limitPrice = ask.getLimitPrice();
		this.originalAmount = ask.getOriginalAmount();
		this.timeStamp = timeStamp;
		this.originalSum = originalSum;
		this.finalSum = finalSum;
	}
}
