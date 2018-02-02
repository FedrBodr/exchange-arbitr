package ru.fedrbodr.exchangearbitr.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Table(indexes={@Index(columnList="exchangeTimeStamp"), @Index(columnList="createTime")})
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
	@Column()
	private Date exchangeTimeStamp;
	@Column(nullable = false)
	private Date createTime;
	@ManyToOne
	@JoinColumn(name = "symbol_id")
	private Symbol symbol;

	public MarketPosition(Exchange exchange, Symbol symbol, Double price) {
		this.exchange = exchange;
		this.price = price;
		this.symbol = symbol;
		this.createTime = new Date();
	}

	@Override
	public String toString() {
		return "MarketPosition{" +
				"id=" + id +
				", exchangeId=" + exchange +
				", price=" + String.format("%f" , price)+
				", exchangeTimeStamp=" + exchangeTimeStamp +
				", createAt=" + createTime +
				'}';
	}
}
