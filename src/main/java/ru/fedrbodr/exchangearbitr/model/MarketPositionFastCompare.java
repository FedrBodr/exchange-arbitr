package ru.fedrbodr.exchangearbitr.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.fedrbodr.exchangearbitr.dao.model.MarketPositionFast;
import ru.fedrbodr.exchangearbitr.dao.model.UniLimitOrder;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarketPositionFastCompare {
	private MarketPositionFast buyMarketPosition;
	private MarketPositionFast sellMarketPosition;
	private BigDecimal differencePercentCorrect;
	private String buySymbolExchangeUrl;
	private String sellSymbolExchangeUrl;
	private List<UniLimitOrder> sellOrders;
	private List<UniLimitOrder> buyOrders;


	public MarketPositionFastCompare(MarketPositionFast buyMarketPosition, MarketPositionFast sellMarketPosition,
									 BigDecimal differencePercentCorrect) {
		this.buyMarketPosition = buyMarketPosition;
		this.sellMarketPosition = sellMarketPosition;
		this.differencePercentCorrect = differencePercentCorrect;
	}

	public void setSellOrders(List<UniLimitOrder> sellOrders) {
		this.sellOrders = sellOrders;
	}

	public void setBuyOrders(List<UniLimitOrder> buyOrders) {
		this.buyOrders = buyOrders;
	}
}
