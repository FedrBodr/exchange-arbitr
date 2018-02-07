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
	@Column()
	private Date exchangeTimeStamp;
	@Column(nullable = false)
	private Date createTime;
	@ManyToOne
	@JoinColumn(name = "symbol_id")
	private UniSymbol uniSymbol;
	private boolean active;

	public MarketPosition(ExchangeMeta exchangeMeta, UniSymbol uniSymbol, BigDecimal lastPrice, boolean active) {
		this.exchangeMeta = exchangeMeta;
		this.lastPrice = lastPrice;
		this.uniSymbol = uniSymbol;
		this.createTime = new Date();
		this.active = active;
	}

	@Override
	public String toString() {
		return "MarketPosition{" +
				"id=" + id +
				", exchangeId=" + exchangeMeta +
				", lastPrice=" + String.format("%f" , lastPrice)+
				", exchangeTimeStamp=" + exchangeTimeStamp +
				", createAt=" + createTime +
				'}';
	}
}
