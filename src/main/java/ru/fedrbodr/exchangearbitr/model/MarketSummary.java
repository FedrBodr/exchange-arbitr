package ru.fedrbodr.exchangearbitr.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
public class MarketSummary {
	@Id
	@GeneratedValue
	private long id;
	private String marketName;
	private String primaryCurrencyName;
	private String secondaryCurrencyName;

	public MarketSummary(String marketName, String primaryCurrencyName, String secondaryCurrencyName) {
		this.marketName = marketName;
		this.primaryCurrencyName = primaryCurrencyName;
		this.secondaryCurrencyName = secondaryCurrencyName;
	}
}
