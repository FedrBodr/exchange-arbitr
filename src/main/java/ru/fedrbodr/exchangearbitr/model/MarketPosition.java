package ru.fedrbodr.exchangearbitr.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
public class MarketPosition {
	@Id
	@GeneratedValue
	private long id;
	private long exchangeId;
	private Double price;
	@Column(columnDefinition="DATETIME(6)")
	private LocalDateTime timeStamp;
	@Column(columnDefinition="DATETIME(6) NOT NULL")
	private LocalDateTime dbSaveTime;
	@ManyToOne
	@JoinColumn(name = "market_summary_id")
	private MarketSummary marketSummary;

	@Override
	public String toString() {
		return "MarketPosition{" +
				"id=" + id +
				", exchangeId=" + exchangeId +
				", price=" + String.format("%f" , price)+
				", timeStamp=" + timeStamp +
				", dbSaveTime=" + dbSaveTime +
				'}';
	}
}
