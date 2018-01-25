package ru.fedrbodr.exchangearbitr.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
public class MarketPosition {
	@Id
	private long id;
	private String marketName;

	public MarketPosition(String marketName) {
		this.marketName = marketName;
	}
}
