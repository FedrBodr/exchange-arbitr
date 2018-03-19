package ru.fedrbodr.exchangearbitr.dao.longtime.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Table(name = "bullet_fork_filter")
@Entity
@Data
@NoArgsConstructor
public class BulletForkFilter {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	/** Where we buy */
	@ManyToOne()
	@JoinColumn(name = "buy_exchange_id")
	private ExchangeMetaLong buyExchangeMeta;
	/** Where we sell */
	@ManyToOne()
	@JoinColumn(name = "sell_exchange_id")
	private ExchangeMetaLong sellExchangeMeta;
	@ManyToOne()
	@JoinColumn(name = "symbol_id")
	private SymbolLong symbol;

	public BulletForkFilter(ExchangeMetaLong buyExchangeMeta, ExchangeMetaLong sellExchangeMeta) {
		this.buyExchangeMeta = buyExchangeMeta;
		this.sellExchangeMeta = sellExchangeMeta;
	}

	public BulletForkFilter(ExchangeMetaLong buyExchangeMeta, ExchangeMetaLong sellExchangeMeta, SymbolLong symbol) {
		this.buyExchangeMeta = buyExchangeMeta;
		this.sellExchangeMeta = sellExchangeMeta;
		this.symbol = symbol;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof BulletForkFilter)) return false;
		BulletForkFilter that = (BulletForkFilter) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
