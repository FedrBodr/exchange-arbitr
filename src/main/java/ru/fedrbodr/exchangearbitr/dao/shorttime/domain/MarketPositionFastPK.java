package ru.fedrbodr.exchangearbitr.dao.shorttime.domain;

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
	private Symbol symbol;

	public MarketPositionFastPK(ExchangeMeta exchangeMeta, Symbol symbol) {
		this.exchangeMeta = exchangeMeta;
		this.symbol = symbol;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		MarketPositionFastPK that = (MarketPositionFastPK) o;
		return Objects.equals(exchangeMeta, that.exchangeMeta) &&
				Objects.equals(symbol, that.symbol);
	}

	@Override
	public int hashCode() {

		return Objects.hash(super.hashCode(), exchangeMeta, symbol);
	}
}
