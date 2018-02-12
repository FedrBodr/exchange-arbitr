package ru.fedrbodr.exchangearbitr.dao.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Table(indexes={@Index(columnList="exchangeTimeStamp"), @Index(columnList="createTime")})
@Entity
@Data
@NoArgsConstructor
public class MarketPosition implements Serializable{
	@Id
	@GeneratedValue
	private long id;
	@ManyToOne
	@JoinColumn(name = "exchange_id")
	private ExchangeMeta exchangeMeta;
	@Column(precision = 15, scale = 8)
	private BigDecimal lastPrice;
	@Column(precision = 15, scale = 8)
	private BigDecimal bidPrice;
	@Column(precision = 15, scale = 8)
	private BigDecimal askPrice;
	@Column()
	private Date exchangeTimeStamp;
	@Column(nullable = false)
	private Date createTime;
	@ManyToOne
	@JoinColumn(name = "symbol_id")
	private SymbolPair symbolPair;
	private boolean active;

	public MarketPosition(ExchangeMeta exchangeMeta, SymbolPair symbolPair, BigDecimal lastPrice, BigDecimal bidPrice, BigDecimal askPrice, boolean active) {
		this.exchangeMeta = exchangeMeta;
		this.lastPrice = lastPrice;
		this.bidPrice = bidPrice;
		this.askPrice = askPrice;
		this.symbolPair = symbolPair;
		this.active = active;
		this.createTime = new Date();
	}

	@Override
	public String toString() {
		return "MarketPosition{" +
				"id=" + id +
				", exchangeId=" + exchangeMeta +
				", lastPrice=" + String.format("%f" , lastPrice)+
				", askPrice=" + String.format("%f" , askPrice)+
				", bidPrice=" + String.format("%f" , bidPrice)+
				", exchangeTimeStamp=" + exchangeTimeStamp +
				", createAt=" + createTime +
				'}';
	}
}
