package ru.fedrbodr.exchangearbitr.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Table(indexes={@Index(columnList="timeStamp"), @Index(columnList="createTime")})
@Entity
@Data
@NoArgsConstructor
public class MarketPositionFast implements Serializable{

	@EmbeddedId
	protected MarketPositionFastPK marketPositionFastPK;

	private Double price;
	@Column(columnDefinition="DATETIME(6)")
	private LocalDateTime timeStamp;
	@Column(columnDefinition="DATETIME(6) NOT NULL")
	private LocalDateTime createTime;

	public MarketPositionFast(MarketPosition marketPosition) {
		marketPositionFastPK = new MarketPositionFastPK();
		this.marketPositionFastPK.setExchange(marketPosition.getExchange());
		this.marketPositionFastPK.setSymbol(marketPosition.getSymbol());
		this.price = marketPosition.getPrice();

		this.timeStamp = marketPosition.getTimeStamp();
		this.createTime = LocalDateTime.now();
	}
}
