package ru.fedrbodr.exchangearbitr.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.dao.MarketPositionFastRepositoryCustom;
import ru.fedrbodr.exchangearbitr.model.MarketPositionFastCompare;
import ru.fedrbodr.exchangearbitr.model.dao.MarketPositionFast;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class MarketPositionFastServiceImpl implements MarketPositionFastService {
	@Autowired
	private MarketPositionFastRepositoryCustom marketPositionFastRepositoryCustom;
	@Override
	public List<MarketPositionFastCompare> getTopAfter12MarketPositionFastCompareList() {
		List<Object[]> topMarketPositionDif = marketPositionFastRepositoryCustom.getTopAfter12MarketPositionFastCompareList();
		return calculateDifferences(topMarketPositionDif);
	}

	@Override
	public List<MarketPositionFastCompare> getTopFullMarketPositionFastCompareList() {
		List<Object[]> topMarketPositionDif = marketPositionFastRepositoryCustom.getTopFullMarketPositionFastCompareList();

		return calculateDifferences(topMarketPositionDif);
	}

	@Override
	public List<MarketPositionFastCompare> getTopProblemMarketPositionFastCompareList() {
		List<Object[]> topMarketPositionDif = marketPositionFastRepositoryCustom.getTopProblemMarketPositionFastCompareList();
		return calculateDifferences(topMarketPositionDif);
	}

	private List<MarketPositionFastCompare> calculateDifferences(List<Object[]> topMarketPositionDif) {
		List<MarketPositionFastCompare> marketPositionFastCompares = new ArrayList<>();

		topMarketPositionDif.forEach(marketPositionDif -> {

			MarketPositionFast marketPositionFast =(MarketPositionFast) marketPositionDif[0];
			MarketPositionFast marketPositionFastToCompare =(MarketPositionFast) marketPositionDif[1];
			MarketPositionFast marketPositionBuy;
			MarketPositionFast marketPositionSell;
			if(marketPositionFast.getLastPrice().compareTo(marketPositionFastToCompare.getLastPrice()) < 1){
				marketPositionBuy = marketPositionFast;
				marketPositionSell = marketPositionFastToCompare;

			}else{
				marketPositionBuy = marketPositionFastToCompare;
				marketPositionSell = marketPositionFast;
			}
			marketPositionFastCompares.add(new MarketPositionFastCompare(
					marketPositionBuy,
					marketPositionSell,
					marketPositionSell.getLastPrice().subtract(marketPositionBuy.getLastPrice()),
							(marketPositionSell.getLastPrice().subtract(marketPositionBuy.getLastPrice())).
									divide(marketPositionSell.getLastPrice(),10, RoundingMode.HALF_UP).
									multiply(new BigDecimal(100)),
					marketPositionBuy.getLastPrice().divide(marketPositionSell.getLastPrice(),8, RoundingMode.HALF_UP).multiply(new BigDecimal(100)),
					(marketPositionBuy.getLastPrice().divide(marketPositionSell.getLastPrice(),8, RoundingMode.HALF_UP).subtract(new BigDecimal(-1))).subtract(new BigDecimal(100))
			));

		});

		return marketPositionFastCompares;
	}
}
