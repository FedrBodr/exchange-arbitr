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
 * if interval between forks longest then last grabbing time - it is new fork! and new fork_history_id.
 *
*
* */
@Table()
@Entity
@Data
@NoArgsConstructor
public class Fork {
	@EmbeddedId
	private ForkPK forkPK;
	/** Creation time, in fact, it is time after getting the response from api and before saving to db. */
	@Column(nullable = false)
	private Date timestamp;

	/** Profit list by some deposit */
	@OneToMany()
	private List<DepoProfit> profits;
}
