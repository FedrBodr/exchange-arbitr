package ru.fedrbodr.exchangearbitr.dao.model;

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
	private SymbolPair symbolPair;

	public UniLimitOrderPK(Long id, ExchangeMeta exchangeMeta, SymbolPair symbolPair, Order.OrderType type) {
		this.id = id;
		this.exchangeMeta = exchangeMeta;
		this.symbolPair = symbolPair;
		this.type = type;
	}
}
