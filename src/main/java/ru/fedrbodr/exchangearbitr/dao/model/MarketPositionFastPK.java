package ru.fedrbodr.exchangearbitr.dao.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		MarketPositionFastPK that = (MarketPositionFastPK) o;
		return Objects.equals(exchangeMeta, that.exchangeMeta) &&
				Objects.equals(symbolPair, that.symbolPair);
	}

	@Override
	public int hashCode() {

		return Objects.hash(super.hashCode(), exchangeMeta, symbolPair);
	}
}
