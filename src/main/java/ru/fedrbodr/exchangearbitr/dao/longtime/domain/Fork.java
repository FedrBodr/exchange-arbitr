package ru.fedrbodr.exchangearbitr.dao.longtime.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Fork - represent info about where and what(altcoin) buy and then sell to get profit.
 * Contains profit info by datetime and several amount,
 * fork_history_id - id for several forks with different time but at the one time period - like 18:19:10.333,18:19:10.993,18:19:11.333
 * if interval between forks longest then last grabbing time - it is new fork! and new fork_pack_id.
 *
*
* */
@Table(name = "fork")
@Entity
@Data
@NoArgsConstructor
public class Fork {
	@Id
	@Column(name = "id")
	@GeneratedValue
	private Long id;
	/**
	 * When new fork detected - window open, then all forks whiten with this window id,
	 * then if the time has passed, new window id is.
	 *
	 * */
	@Column(name = "fork_window_id")
	private Long forkWindowId;
	/**
	 * Where we buy
	* */
	@ManyToOne()
	@JoinColumn(name = "buy_exchange_id")
	private ExchangeMetaLong buyExchangeMeta;
	/**
	 * Where we sell
	 * */
	@ManyToOne()
	@JoinColumn(name = "sell_exchange_id")
	private ExchangeMetaLong sellExchangeMeta;
	@ManyToOne()
	@JoinColumn(name = "symbol_id")
	private SymbolLong symbol;
	/** Creation time, in fact, it is time after getting the response from api and before saving to db. */
	@Column(nullable = false)
	private Date timestamp;

	/** Profit list by some deposit */
	@OneToMany(cascade=CascadeType.PERSIST)
	private List<DepoProfit> profits;

	@Override
	public String toString() {
		return "Fork{" +
				"id=" + id +
				", forkWindowId=" + forkWindowId +
				", buyExchangeMeta=" + buyExchangeMeta.getExchangeName() +
				", sellExchangeMeta=" + sellExchangeMeta.getExchangeName() +
				", symbol=" + symbol.getName() +
				", timestamp=" + timestamp +
				'}';
	}
}
