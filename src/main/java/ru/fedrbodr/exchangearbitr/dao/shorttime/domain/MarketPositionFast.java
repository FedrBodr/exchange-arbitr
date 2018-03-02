package ru.fedrbodr.exchangearbitr.dao.shorttime.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@Table( name = "market_position_fast")
@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
public class MarketPositionFast implements Serializable{
	@EmbeddedId
	protected MarketPositionFastPK marketPositionFastPK;
	@Column(name = "last_price", precision = 15, scale = 8)
	private BigDecimal lastPrice;
	@Column(name = "bid_price", precision = 15, scale = 8)
	private BigDecimal bidPrice;
	@Column(name = "ask_price", precision = 15, scale = 8)
	private BigDecimal askPrice;
	/* TODO think twice when i will review maybe it is not needed(only one exchange return time in ticker23) */
	@Column(name = "exchange_time_stamp")
	private Date exchangeTimeStamp;
	@Column(name = "create_at", nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@CreatedDate
	private Date createAt;
	@Column(name = "updated_at", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@LastModifiedDate
	private Date updatedAt;
	private boolean active;

	public MarketPositionFast(MarketPosition marketPosition) {
		marketPositionFastPK = new MarketPositionFastPK(marketPosition.getExchangeMeta(), marketPosition.getSymbol());
		this.lastPrice = marketPosition.getLastPrice();
		this.bidPrice = marketPosition.getBidPrice();
		this.askPrice = marketPosition.getAskPrice();
		this.exchangeTimeStamp = marketPosition.getExchangeTimeStamp();
		this.active = marketPosition.isActive();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MarketPositionFast that = (MarketPositionFast) o;
		return Objects.equals(marketPositionFastPK, that.marketPositionFastPK);
	}

	@Override
	public int hashCode() {
		return Objects.hash(marketPositionFastPK.hashCode());
	}
}
