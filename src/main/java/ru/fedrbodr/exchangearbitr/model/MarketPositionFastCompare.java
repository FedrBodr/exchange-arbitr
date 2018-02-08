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
	private BigDecimal differencePercentCorrect;
	private BigDecimal differencePercent;
	private BigDecimal differencePercentToni;
	private String buySymbolExchangeUrl;
	private String sellSymbolExchangeUrl;


	public MarketPositionFastCompare(MarketPositionFast buyMarketPosition, MarketPositionFast sellMarketPosition,
									 BigDecimal differencePercentCorrect) {
		this.buyMarketPosition = buyMarketPosition;
		this.sellMarketPosition = sellMarketPosition;
		this.differencePercentCorrect = differencePercentCorrect;
	}
}
