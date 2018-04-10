package ru.fedrbodr.exchangearbitr.view;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.DepoProfit;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.User;
import ru.fedrbodr.exchangearbitr.dao.longtime.reports.ForkInfo;
import ru.fedrbodr.exchangearbitr.services.ForkService;
import ru.fedrbodr.exchangearbitr.services.UserService;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Controller
@Scope("view")
@Slf4j
public class ForksController implements Serializable {
	public static double HIGEST_PROFIT_FOR_REAL_FORKS = 11;
	@Autowired
	private ForkService forkService;
	@Autowired
	private UserService userService;
	private List<ForkInfo> filteredForkInfos;
	private List<ForkInfo> currentForks;
	private Double minDeposit = Double.valueOf(0);
	private Double minProfit = new Double(0.5);

	private List<ForkInfo> unrealFilteredForkInfos;
	private List<ForkInfo> unrealCurrentForks;

	public List<ForkInfo> getCurrentForks() {
		if (currentForks == null) {
			initCurrentForks();
		}

		return currentForks;
	}

	public List<ForkInfo> getUnrealCurrentForks() {
		if (unrealCurrentForks == null) {
			initCurrentForks();
		}

		return unrealCurrentForks;
	}

	private void initCurrentForks() {
		currentForks = new LinkedList<>();
		unrealCurrentForks = new LinkedList<>();
		User currentUserOrNull = userService.getCurrentUserOrNull();
		List<ForkInfo> allForks;
		allForks = forkService.getCurrentForks();

		for (ForkInfo fork : allForks) {
			if (fork.getProfits().size() == 0 || fork.getProfits().size() > 0 && fork.getProfits().get(0).getProfit().multiply(BigDecimal.valueOf(100)).compareTo(BigDecimal.valueOf(0.54)) < 0) {
				/* hide small profit fork - its used by self for auto raiding */
			} else if (fork.getProfits().size() > 1 && fork.getProfits().get(1).getProfit().multiply(BigDecimal.valueOf(100)).compareTo(BigDecimal.valueOf(HIGEST_PROFIT_FOR_REAL_FORKS)) > 0 ||
					fork.getProfits().size() > 0 && fork.getProfits().get(0).getProfit().multiply(BigDecimal.valueOf(100)).compareTo(BigDecimal.valueOf(HIGEST_PROFIT_FOR_REAL_FORKS)) > 0) {
				unrealCurrentForks.add(fork);
			} else if (suitableByUserFilters(fork)) {
				currentForks.add(fork);
			} else {
				// not show
			}
		}
	}

	public String filter(){
		initCurrentForks();
		return "";
	}

	private boolean suitableByUserFilters(ForkInfo fork) {
		List<DepoProfit> profits = fork.getProfits();
		for (DepoProfit profit : profits) {
			if (profit.getDeposit().compareTo(BigDecimal.valueOf(minDeposit)) >= 0 && profit.getProfit().multiply(BigDecimal.valueOf(100)).compareTo(BigDecimal.valueOf(minProfit)) >= 0) {
				return true;
			}
		}
		return false;
	}

	public Map<String, String> getDepositsFilterList() {
		Map<String, String> depositsMap = new LinkedHashMap<>();
		depositsMap.put("0.01", "0.01");
		depositsMap.put("0.1", "0.1");
		depositsMap.put("0.25", "0.25");
		depositsMap.put("0.5", "0.5");
		depositsMap.put("1", "1");
		depositsMap.put("2", "2");
		depositsMap.put("4", "4");
		depositsMap.put("8", "8");
		depositsMap.put("10", "10");
		depositsMap.put("15", "15");
		depositsMap.put("100", "100");
		depositsMap.put("1000", "1000");
		depositsMap.put("2000", "2000");
		depositsMap.put("4000", "4000");
		depositsMap.put("10000", "10000");
		return depositsMap;
	}

	public List<ForkInfo> getFilteredForkInfos() {
		List list = new LinkedList();
		if(filteredForkInfos!=null) {
			for (ForkInfo filteredForkInfo : filteredForkInfos) {
				list.add(filteredForkInfo);
			}

			return list;
		}else {
			return filteredForkInfos;
		}
	}

	public void setFilteredForkInfos(List<ForkInfo> filteredForkInfos) {
		this.filteredForkInfos = filteredForkInfos;
	}

	public List<ForkInfo> getUnrealFilteredForkInfos() {
		return unrealFilteredForkInfos;
	}

	public void setUnrealFilteredForkInfos(List<ForkInfo> unrealFilteredForkInfos) {
		this.unrealFilteredForkInfos = unrealFilteredForkInfos;
	}

	public Double getMinDeposit() {
		return minDeposit;
	}

	public void setMinDeposit(Double minDeposit) {
		this.minDeposit = minDeposit;
	}

	public Double getMinProfit() {
		return minProfit;
	}

	public void setMinProfit(Double minProfit) {
		this.minProfit = minProfit;
	}
}
