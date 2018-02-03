package ru.fedrbodr.exchangearbitr.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.dao.MarketPositionFastRepositoryCustom;
import ru.fedrbodr.exchangearbitr.model.MarketPositionFastCompare;
import ru.fedrbodr.exchangearbitr.model.dao.MarketPositionFast;

import java.util.ArrayList;
import java.util.List;

@Service
public class MarketPositionFastServiceImpl implements MarketPositionFastService {
	@Autowired
	private MarketPositionFastRepositoryCustom marketPositionFastRepositoryCustom;
	@Override
	public List<MarketPositionFastCompare> getTopMarketPositionFastCompareList() {
		List<Object[]> topMarketPositionDif = marketPositionFastRepositoryCustom.getTopMarketPositionFastCompareList();
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
					marketPositionSell.getLastPrice().floatValue() / marketPositionBuy.getLastPrice().floatValue() / 100
			));

		});
		/*Collections.reverse(marketPositionFastCompares);*/
		return marketPositionFastCompares;
	}
}
