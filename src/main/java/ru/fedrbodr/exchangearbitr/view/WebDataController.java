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

	private MarketPositionFastCompare marketPositionFastSelected;

	public List<MarketPositionFastCompare> getTopAfter10MarketPositionFastCompareList(){
		return marketPositionFastService.getTopAfter10MarketPositionFastCompareList();
	}

	public List<MarketPositionFastCompare> getTopProblemAfter10MarketPositionFastCompareList(){
		return marketPositionFastService.getTopProblemAfter10MarketPositionFastCompareList();
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

	public MarketPositionFastCompare getMarketPositionFastSelected() {
		return marketPositionFastSelected;
	}

	public void setMarketPositionFastSelected(MarketPositionFastCompare marketPositionFastSelected) {
		this.marketPositionFastSelected = marketPositionFastSelected;
	}
}
