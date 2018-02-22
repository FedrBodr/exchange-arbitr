package ru.fedrbodr.exchangearbitr.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.MarketPositionFast;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.UniLimitOrder;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarketPositionFastCompare {
	private int id;
	private MarketPositionFast buyMarketPosition;
	private MarketPositionFast sellMarketPosition;
	private BigDecimal differencePercentCorrect;
	private String buySymbolExchangeUrl;
	private String sellSymbolExchangeUrl;
	private List<UniLimitOrder> sellOrders;
	private List<UniLimitOrder> buyOrders;
	private List<DepositProfit> depositProfitList;
	private boolean lieProfitByGlasses;
	private boolean publicVisible;


	public MarketPositionFastCompare(MarketPositionFast buyMarketPosition, MarketPositionFast sellMarketPosition,
									 BigDecimal differencePercentCorrect) {
		this.buyMarketPosition = buyMarketPosition;
		this.sellMarketPosition = sellMarketPosition;
		this.differencePercentCorrect = differencePercentCorrect;
	}
}
