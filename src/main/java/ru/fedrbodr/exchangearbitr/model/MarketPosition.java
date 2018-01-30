package ru.fedrbodr.exchangearbitr.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class MarketPosition {
	@Id
	@GeneratedValue
	private long id;
	@ManyToOne
	@JoinColumn(name = "exchange_id")
	private Exchange exchange;
	private Double price;
	@Column(columnDefinition="DATETIME(6)")
	private LocalDateTime timeStamp;
	@Column(columnDefinition="DATETIME(6) NOT NULL")
	private LocalDateTime createTime;
	@ManyToOne
	@JoinColumn(name = "symbol_id")
	private Symbol symbol;

	public MarketPosition(Exchange exchange, Symbol symbol, Double price) {
		this.exchange = exchange;
		this.price = price;
		this.symbol = symbol;
		this.createTime = LocalDateTime.now();
	}

	@Override
	public String toString() {
		return "MarketPosition{" +
				"id=" + id +
				", exchangeId=" + exchange +
				", price=" + String.format("%f" , price)+
				", timeStamp=" + timeStamp +
				", createTime=" + createTime +
				'}';
	}
}
