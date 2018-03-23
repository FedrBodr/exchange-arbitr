package ru.fedrbodr.exchangearbitr.view;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import ru.fedrbodr.exchangearbitr.dao.longtime.reports.ForkInfo;
import ru.fedrbodr.exchangearbitr.services.ForkService;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

@Controller
@Scope("view")
@Slf4j
public class ForksController implements Serializable {
	@Autowired
	private ForkService forkService;
	private List<ForkInfo> filteredForkInfos;
	private List<ForkInfo> currentForks;

	private List<ForkInfo> unrealFilteredForkInfos;
	private List<ForkInfo> unrealCurrentForks;



	public List<ForkInfo> getCurrentForks(){
		if(currentForks == null){
			initCurrentForks();
		}

		return currentForks;
	}

	public List<ForkInfo> getUnrealCurrentForks(){
		if(unrealCurrentForks == null){
			initCurrentForks();
		}

		return unrealCurrentForks;
	}

	private void initCurrentForks() {
		currentForks = new LinkedList<>();
		unrealCurrentForks = new LinkedList<>();
		List<ForkInfo> allForks = forkService.getCurrentForks();
		for (ForkInfo fork : allForks) {
			if(fork.getProfits().size()==0 || fork.getProfits().size() > 0 && fork.getProfits().get(0).getProfit().multiply(BigDecimal.valueOf(100)).compareTo(BigDecimal.valueOf(0.54)) < 0){
				/* hide small profit fork - its used by self for auto raiding */
			}else if(fork.getProfits().size() > 1 && fork.getProfits().get(1).getProfit().multiply(BigDecimal.valueOf(100)).compareTo(BigDecimal.valueOf(11))>0 ||
					fork.getProfits().size() > 0 && fork.getProfits().get(0).getProfit().multiply(BigDecimal.valueOf(100)).compareTo(BigDecimal.valueOf(11))>0){
				unrealCurrentForks.add(fork);
			}else{
				currentForks.add(fork);
			}

		}
	}

	public List<ForkInfo> getFilteredForkInfos() {
		return filteredForkInfos;
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
}
