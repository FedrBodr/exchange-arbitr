package ru.fedrbodr.exchangearbitr.model.dao;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
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
	@Column(precision = 14, scale = 8)
	private BigDecimal lastPrice;
	@Column()
	private Date exchangeTimeStamp;
	@Column(nullable = false)
	private Date createTime;
	@ManyToOne
	@JoinColumn(name = "symbol_id")
	private Symbol symbol;

	public MarketPosition(Exchange exchange, Symbol symbol, BigDecimal lastPrice) {
		this.exchange = exchange;
		this.lastPrice = lastPrice;
		this.symbol = symbol;
		this.createTime = new Date();
	}

	@Override
	public String toString() {
		return "MarketPosition{" +
				"id=" + id +
				", exchangeId=" + exchange +
				", lastPrice=" + String.format("%f" , lastPrice)+
				", exchangeTimeStamp=" + exchangeTimeStamp +
				", createAt=" + createTime +
				'}';
	}
}
