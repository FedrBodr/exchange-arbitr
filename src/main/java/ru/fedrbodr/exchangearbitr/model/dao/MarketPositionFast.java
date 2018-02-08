package ru.fedrbodr.exchangearbitr.model.dao;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Table
@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
public class MarketPositionFast implements Serializable{

	@EmbeddedId
	protected MarketPositionFastPK marketPositionFastPK;
	@Column(precision = 14, scale = 8)
	private BigDecimal lastPrice;
	@Column(precision = 14, scale = 8)
	private BigDecimal bidPrice;
	@Column(precision = 14, scale = 8)
	private BigDecimal ascPrice;
	/* TODO think twice when i will review maybe it is not needed(only one exchange return time in ticker23) */
	private Date exchangeTimeStamp;
	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@CreatedDate
	private Date createAt;
	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@LastModifiedDate
	private Date updatedAt;
	private boolean active;

	public MarketPositionFast(MarketPosition marketPosition) {
		marketPositionFastPK = new MarketPositionFastPK(marketPosition.getExchangeMeta(), marketPosition.getUniSymbol());
		this.lastPrice = marketPosition.getLastPrice();
		this.exchangeTimeStamp = marketPosition.getExchangeTimeStamp();
		this.active = marketPosition.isActive();
	}
}
