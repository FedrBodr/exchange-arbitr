package ru.fedrbodr.exchangearbitr.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.dao.MarketPositionFastRepositoryCustom;
import ru.fedrbodr.exchangearbitr.model.dao.MarketPositionFast;
import ru.fedrbodr.exchangearbitr.model.dao.MarketPositionFastCompare;

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
			if(marketPositionFast.getPrice()<marketPositionFastToCompare.getPrice()){
				marketPositionBuy = marketPositionFast;
				marketPositionSell = marketPositionFastToCompare;

			}else{
				marketPositionBuy = marketPositionFastToCompare;
				marketPositionSell = marketPositionFast;
			}
			marketPositionFastCompares.add(new MarketPositionFastCompare(
					marketPositionBuy,
					marketPositionSell,
					marketPositionSell.getPrice() - marketPositionBuy.getPrice(),
					(marketPositionSell.getPrice() - marketPositionBuy.getPrice())*100
			));

		});
		/*Collections.reverse(marketPositionFastCompares);*/
		return marketPositionFastCompares;
	}
}
