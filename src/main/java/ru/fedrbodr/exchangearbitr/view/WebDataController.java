package ru.fedrbodr.exchangearbitr.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.ExchangeMetaLong;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.SymbolLong;
import ru.fedrbodr.exchangearbitr.dao.longtime.repo.ExchangeMetaLongRepository;
import ru.fedrbodr.exchangearbitr.dao.longtime.reports.ForkInfo;
import ru.fedrbodr.exchangearbitr.model.MarketPositionFastCompare;
import ru.fedrbodr.exchangearbitr.services.ForkService;
import ru.fedrbodr.exchangearbitr.services.MarketPositionFastService;
import ru.fedrbodr.exchangearbitr.services.SymbolLongService;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Controller
@Scope("session")
public class WebDataController {
	@Autowired
	private MarketPositionFastService marketPositionFastService;
	@Autowired
	private ForkService forkService;
	@Autowired
	private SymbolLongService symbolLongService;
	@Autowired
	private ExchangeMetaLongRepository exchangeMetaLongRepository;
	private List<ForkInfo> filteredForkInfos;
	private List<MarketPositionFastCompare> filteredCompareList;
	private MarketPositionFastCompare marketPositionFastSelected;
	private List<SymbolLong> symbolList;
	private List<SelectItem> exchangeMetaSelectItemsList;

	public List<ForkInfo> getCurrentForks(){
		return forkService.getCurrentForks();
	}

	public List<String> getExchangeMetaLongList(){
		List<String> exchangeNames = new ArrayList<>();
		List<ExchangeMetaLong> allExchangeMetaLongs = exchangeMetaLongRepository.findAll();
		for (ExchangeMetaLong allExchangeMetaLong : allExchangeMetaLongs) {
			exchangeNames.add(allExchangeMetaLong.getExchangeName());
		}
		return exchangeNames;
	}

	public List<SelectItem> getExchangeMetaSelectItemsList() {
		List<ExchangeMetaLong> allExchangeMetaLongs = exchangeMetaLongRepository.findAll();
		exchangeMetaSelectItemsList = new LinkedList<>();
		for (ExchangeMetaLong exchangeMetaLong : allExchangeMetaLongs) {
			exchangeMetaSelectItemsList.add(new SelectItem(exchangeMetaLong, exchangeMetaLong.getExchangeName()));
		}
		return exchangeMetaSelectItemsList;
	}

	public List<SelectItem> getSymbolSelectItemsList() {
		List<SymbolLong> all = symbolLongService.getAll();
		exchangeMetaSelectItemsList = new LinkedList<>();
		for (SymbolLong symbolLong : all) {
			exchangeMetaSelectItemsList.add(new SelectItem(symbolLong, symbolLong.getName()));
		}

		return exchangeMetaSelectItemsList;
	}

	public List<MarketPositionFastCompare> getTopAfter10MarketPositionFastCompareList(){
		return marketPositionFastService.getTopAfter10MarketPositionFastCompareList();
	}

	public List<MarketPositionFastCompare> getTopProblemAfter10MarketPositionFastCompareList(){
		return marketPositionFastService.getTopProblemAfterMarketPositionFastCompareList();
	}

	public List<MarketPositionFastCompare> getAdminMarketPositionCompareList(){
		return marketPositionFastService.getTopMarketPositionFastCompareList();
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

	public List<ForkInfo> getFilteredForkInfos() {
		return filteredForkInfos;
	}

	public void setFilteredForkInfos(List<ForkInfo> filteredForkInfos) {
		this.filteredForkInfos = filteredForkInfos;
	}


}
