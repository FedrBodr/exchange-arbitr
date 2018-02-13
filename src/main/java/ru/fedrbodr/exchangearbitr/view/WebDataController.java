package ru.fedrbodr.exchangearbitr.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.fedrbodr.exchangearbitr.model.MarketPositionFastCompare;
import ru.fedrbodr.exchangearbitr.services.MarketPositionFastService;

import java.util.List;

@Controller
public class WebDataController {
	@Autowired
	private MarketPositionFastService marketPositionFastService;

	private List<MarketPositionFastCompare> filteredCompareList;

	public List<MarketPositionFastCompare> getTopAfter12MarketPositionFastCompareList(){
		return marketPositionFastService.getTopAfter12MarketPositionFastCompareList();
	}

	public List<MarketPositionFastCompare> getTopProblemMarketPositionFastCompareList(){
		return marketPositionFastService.getTopProblemMarketPositionFastCompareList();
	}

	public List<MarketPositionFastCompare> getAdminMarketPositionCompareList(){
		return marketPositionFastService.getTopFullMarketPositionFastCompareList();
	}

	public List<MarketPositionFastCompare> getFilteredCompareList() {
		return filteredCompareList;
	}

	public void setFilteredCompareList(List<MarketPositionFastCompare> filteredCompareList) {
		this.filteredCompareList = filteredCompareList;
	}
}
