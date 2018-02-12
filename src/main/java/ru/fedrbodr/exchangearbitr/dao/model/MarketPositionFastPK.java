package ru.fedrbodr.exchangearbitr.dao.model;

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
	private ExchangeMeta exchangeMeta;
	@ManyToOne
	@JoinColumn(name = "symbol_id")
	private SymbolPair symbolPair;

	public MarketPositionFastPK(ExchangeMeta exchangeMeta, SymbolPair symbolPair) {
		this.exchangeMeta = exchangeMeta;
		this.symbolPair = symbolPair;
	}
}
