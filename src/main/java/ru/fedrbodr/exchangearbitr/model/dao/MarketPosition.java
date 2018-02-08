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
	private ExchangeMeta exchangeMeta;
	@Column(precision = 14, scale = 8)
	private BigDecimal lastPrice;
	@Column(precision = 14, scale = 8)
	private BigDecimal bidPrice;
	@Column(precision = 14, scale = 8)
	private BigDecimal ascPrice;
	@Column()
	private Date exchangeTimeStamp;
	@Column(nullable = false)
	private Date createTime;
	@ManyToOne
	@JoinColumn(name = "symbol_id")
	private UniSymbol uniSymbol;
	private boolean active;

	public MarketPosition(ExchangeMeta exchangeMeta, UniSymbol uniSymbol, BigDecimal lastPrice, BigDecimal bidPrice, BigDecimal ascPrice, boolean active) {
		this.exchangeMeta = exchangeMeta;
		this.lastPrice = lastPrice;
		this.bidPrice = bidPrice;
		this.ascPrice = ascPrice;
		this.uniSymbol = uniSymbol;
		this.active = active;
		this.createTime = new Date();
	}

	@Override
	public String toString() {
		return "MarketPosition{" +
				"id=" + id +
				", exchangeId=" + exchangeMeta +
				", lastPrice=" + String.format("%f" , lastPrice)+
				", askPrice=" + String.format("%f" , ascPrice)+
				", bidPrice=" + String.format("%f" , bidPrice)+
				", exchangeTimeStamp=" + exchangeTimeStamp +
				", createAt=" + createTime +
				'}';
	}
}
