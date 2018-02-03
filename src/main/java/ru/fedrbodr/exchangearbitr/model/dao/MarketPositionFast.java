package ru.fedrbodr.exchangearbitr.model.dao;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table()
@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
public class MarketPositionFast implements Serializable{

	@EmbeddedId
	protected MarketPositionFastPK marketPositionFastPK;

	private Double price;
	private Date exchangeTimeStamp;
	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@CreatedDate
	private Date createAt;
	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@LastModifiedDate
	private Date updatedAt;

	public MarketPositionFast(MarketPosition marketPosition) {
		marketPositionFastPK = new MarketPositionFastPK();
		this.marketPositionFastPK.setExchange(marketPosition.getExchange());
		this.marketPositionFastPK.setSymbol(marketPosition.getSymbol());
		this.price = marketPosition.getLastPrice();
		this.exchangeTimeStamp = marketPosition.getExchangeTimeStamp();
	}
}
