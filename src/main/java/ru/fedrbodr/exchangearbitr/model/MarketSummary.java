package ru.fedrbodr.exchangearbitr.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
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
	@Column(unique=true)
	private String name;
	private String primaryCurrencyName;
	private String secondaryCurrencyName;

	public MarketSummary(String name, String primaryCurrencyName, String secondaryCurrencyName) {
		this.name = name;
		this.primaryCurrencyName = primaryCurrencyName;
		this.secondaryCurrencyName = secondaryCurrencyName;
	}
}
