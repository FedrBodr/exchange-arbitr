package ru.fedrbodr.exchangearbitr.dao.shorttime.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.knowm.xchange.dto.Order;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@Data
public class UniLimitOrderPK implements Serializable{
	private Long id;
	@Enumerated(EnumType.STRING)
	private Order.OrderType type;
	@ManyToOne
	@JoinColumn(name = "exchange_id")
	private ExchangeMeta exchangeMeta;
	@ManyToOne
	@JoinColumn(name = "symbol_id")
	private Symbol symbol;

	public UniLimitOrderPK(Long id, ExchangeMeta exchangeMeta, Symbol symbol, Order.OrderType type) {
		this.id = id;
		this.exchangeMeta = exchangeMeta;
		this.symbol = symbol;
		this.type = type;
	}
}
