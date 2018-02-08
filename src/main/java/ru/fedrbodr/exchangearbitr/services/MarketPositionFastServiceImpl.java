package ru.fedrbodr.exchangearbitr.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.dao.MarketPositionFastRepositoryCustom;
import ru.fedrbodr.exchangearbitr.model.MarketPositionFastCompare;
import ru.fedrbodr.exchangearbitr.model.dao.MarketPositionFast;
import ru.fedrbodr.exchangearbitr.utils.SymbolsNamesUtils;

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
		return calculateDifferencesForWeb(topMarketPositionDif);
	}

	@Override
	public List<MarketPositionFastCompare> getTopFullMarketPositionFastCompareList() {
		List<Object[]> topMarketPositionDif = marketPositionFastRepositoryCustom.getTopFullMarketPositionFastCompareList();

		return calculateDifferencesForWeb(topMarketPositionDif);
	}

	@Override
	public List<MarketPositionFastCompare> getTopProblemMarketPositionFastCompareList() {
		List<Object[]> topMarketPositionDif = marketPositionFastRepositoryCustom.getTopProblemMarketPositionFastCompareList();
		return calculateDifferencesForWeb(topMarketPositionDif);
	}

	private List<MarketPositionFastCompare> calculateDifferencesForWeb(List<Object[]> topMarketPositionDif) {
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
			MarketPositionFastCompare marketPositionFastCompare = new MarketPositionFastCompare(
					marketPositionBuy,
					marketPositionSell,
					(marketPositionSell.getAscPrice().subtract(marketPositionBuy.getBidPrice())).
							divide(marketPositionSell.getAscPrice(), 3, RoundingMode.HALF_UP));

			marketPositionFastCompare.setBuySymbolExchangeUrl(SymbolsNamesUtils.determineUrlToSymbolMarket(marketPositionBuy));
			marketPositionFastCompare.setSellSymbolExchangeUrl(SymbolsNamesUtils.determineUrlToSymbolMarket(marketPositionSell));

			marketPositionFastCompares.add(marketPositionFastCompare);

		});

		return marketPositionFastCompares;
	}
}
