package ru.fedrbodr.exchangearbitr.model.dao;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@Data
public class MarketPositionFastPK implements Serializable{
	@ManyToOne
	@JoinColumn(name = "exchange_id")
	private Exchange exchange;
	@ManyToOne
	@JoinColumn(name = "symbol_id")
	private Symbol symbol;


	public MarketPositionFastPK(Exchange exchange, Symbol symbol) {
		this.exchange = exchange;
		this.symbol = symbol;
	}
}
