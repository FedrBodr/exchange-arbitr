package ru.fedrbodr.exchangearbitr.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.fedrbodr.exchangearbitr.model.MarketPositionFastCompare;
import ru.fedrbodr.exchangearbitr.services.MarketPositionFastService;

import java.util.List;

@Controller
public class ExchangeSymbolsDifController {
	@Autowired
	private MarketPositionFastService marketPositionFastService;

	public List<MarketPositionFastCompare> getTopAfter12MarketPositionFastCompareList(){
		return marketPositionFastService.getTopAfter12MarketPositionFastCompareList();
	}

	public List<MarketPositionFastCompare> getTopFullMarketPositionFastCompareList(){
		return marketPositionFastService.getTopFullMarketPositionFastCompareList();
	}

	public List<MarketPositionFastCompare> getTopProblemMarketPositionFastCompareList(){
		return marketPositionFastService.getTopProblemMarketPositionFastCompareList();
	}
}
