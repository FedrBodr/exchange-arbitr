package ru.fedrbodr.exchangearbitr.model;

import com.sun.javafx.binding.StringFormatter;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
	private String marketName;
	private String primaryCurrencyName;
	private String secondaryCurrencyName;
	private Double price;
	private LocalDateTime timeStamp;



	public MarketPosition(String marketName) {
		this.marketName = marketName;
	}

	public MarketPosition(String marketName, String primaryCurrencyName, String secondaryCurrencyName, Double price, LocalDateTime timeStamp) {
		this.marketName = marketName;
		this.primaryCurrencyName = primaryCurrencyName;
		this.secondaryCurrencyName = secondaryCurrencyName;
		this.price = price;
		this.timeStamp = timeStamp;
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
