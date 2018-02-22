package ru.fedrbodr.exchangearbitr.dao.shorttime.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Table(indexes={@Index(columnList="exchange_time_stamp"), @Index(columnList="create_time")})
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
	@Column(name = "last_price", precision = 15, scale = 8)
	private BigDecimal lastPrice;
	@Column(name = "bid_price", precision = 15, scale = 8)
	private BigDecimal bidPrice;
	@Column(name = "ask_price", precision = 15, scale = 8)
	private BigDecimal askPrice;
	@Column(name = "exchange_time_stamp")
	private Date exchangeTimeStamp;
	@Column(name = "create_time", nullable = false)
	private Date createTime;
	@ManyToOne
	@JoinColumn(name = "symbol_id")
	private Symbol symbol;
	private boolean active;

	public MarketPosition(ExchangeMeta exchangeMeta, Symbol symbol, BigDecimal lastPrice, BigDecimal bidPrice, BigDecimal askPrice, boolean active) {
		this.exchangeMeta = exchangeMeta;
		this.lastPrice = lastPrice;
		this.bidPrice = bidPrice;
		this.askPrice = askPrice;
		this.symbol = symbol;
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
