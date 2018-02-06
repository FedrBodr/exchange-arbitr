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
	private String buySymbolExchangeUrl;
	private String sellSymbolExchangeUrl;


	public MarketPositionFastCompare(MarketPositionFast buyMarketPosition, MarketPositionFast sellMarketPosition, BigDecimal difference,
									 BigDecimal differencePercentCorrect, BigDecimal differencePercent, BigDecimal differencePercentToni) {
		this.buyMarketPosition = buyMarketPosition;
		this.sellMarketPosition = sellMarketPosition;
		this.difference = difference;
		this.differencePercentCorrect = differencePercentCorrect;
		this.differencePercent = differencePercent;
		this.differencePercentToni = differencePercentToni;
	}
}
