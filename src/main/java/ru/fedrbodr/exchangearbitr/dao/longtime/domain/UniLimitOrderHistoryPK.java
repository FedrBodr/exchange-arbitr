package ru.fedrbodr.exchangearbitr.dao.longtime.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.knowm.xchange.dto.Order;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@Data
public class UniLimitOrderHistoryPK implements Serializable{
	private Long id;
	@Enumerated(EnumType.STRING)
	private Order.OrderType type;
	@ManyToOne
	@JoinColumn(name = "exchange_id")
	private ExchangeMetaLong exchangeMeta;
	@ManyToOne
	@JoinColumn(name = "symbol_id")
	private SymbolLong symbolPair;

	public UniLimitOrderHistoryPK(Long id, ExchangeMetaLong exchangeMeta, SymbolLong symbolPair, Order.OrderType type) {
		this.id = id;
		this.exchangeMeta = exchangeMeta;
		this.symbolPair = symbolPair;
		this.type = type;
	}
}
