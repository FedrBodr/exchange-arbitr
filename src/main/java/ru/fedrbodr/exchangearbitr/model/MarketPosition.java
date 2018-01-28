package ru.fedrbodr.exchangearbitr.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class MarketPosition {
	@Id
	@GeneratedValue
	private long id;
	private long exchangeId;
	private String marketName;
	private String primaryCurrencyName;
	private String secondaryCurrencyName;
	private Double price;
	private LocalDateTime timeStamp;
	private LocalDateTime dbSaveTime;



	public MarketPosition(String marketName) {
		this.marketName = marketName;
	}

	@Override
	public String toString() {
		return "MarketPosition{" +
				"id=" + id +
				", marketName='" + marketName + '\'' +
				", primaryCurrencyName='" + primaryCurrencyName + '\'' +
				", secondaryCurrencyName='" + secondaryCurrencyName + '\'' +
				", price=" + String.format("%f" , price)+
				", timeStamp=" + timeStamp +
				'}';
	}
}
