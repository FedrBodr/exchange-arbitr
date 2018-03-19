package ru.fedrbodr.exchangearbitr.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.DepoProfit;
import ru.fedrbodr.exchangearbitr.dao.longtime.reports.ForkInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class ForkInfoDto {
	private Long id;
	private Long forkWindowId;
	/** Where we buy */
	private ExchangeMetaLongDto sellExchangeMeta;
	/** Where we sell */
	private ExchangeMetaLongDto buyExchangeMeta;
	private SymbolLongDto symbol;
	private List<DepoProfitDto> profits;
	private Date startTime;
	private Date lastUpdatedTime;

	public ForkInfoDto(ForkInfo currentFork) {
		this.id = currentFork.getId();
		this.forkWindowId=currentFork.getForkWindowId();
		this.sellExchangeMeta = new ExchangeMetaLongDto(currentFork.getSellExchangeMeta());
		this.buyExchangeMeta = new ExchangeMetaLongDto(currentFork.getBuyExchangeMeta());
		this.symbol = new SymbolLongDto(currentFork.getSymbol());
		this.profits = convertToDepoProfitList(currentFork.getProfits());
		this.startTime = currentFork.getStartTime();
		this.lastUpdatedTime = currentFork.getLastUpdatedTime();
	}

	private List<DepoProfitDto> convertToDepoProfitList(List<DepoProfit> profits) {
		List<DepoProfitDto> depoProfits = new ArrayList<>();
		for (DepoProfit depositProfit : profits) {
			depoProfits.add(new DepoProfitDto(depositProfit));
		}
		return depoProfits;
	}
}
