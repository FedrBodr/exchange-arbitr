package ru.fedrbodr.exchangearbitr.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.fedrbodr.exchangearbitr.model.dao.MarketPositionFast;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarketPositionFastCompare {
	private MarketPositionFast buyMarketPosition;
	private MarketPositionFast sellMarketPosition;
	private BigDecimal difference;
	private BigDecimal differencePercentCorrect;
	private BigDecimal differencePercent;
	private BigDecimal differencePercentToni;

	public BigDecimal getDifferencePercentCorrect() {
		return differencePercentCorrect;
	}

	public void setDifferencePercentCorrect(BigDecimal differencePercentCorrect) {
		this.differencePercentCorrect = differencePercentCorrect;
	}
}
