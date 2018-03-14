package ru.fedrbodr.exchangearbitr.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import ru.fedrbodr.exchangearbitr.dao.longtime.reports.ForkInfo;
import ru.fedrbodr.exchangearbitr.services.ForkService;

import java.util.List;

@Controller
@Scope("view")
public class ForksController {
	@Autowired
	private ForkService forkService;
	private List<ForkInfo> filteredForkInfos;
	private List<ForkInfo> currentForks;



	public List<ForkInfo> getCurrentForks(){
		if(currentForks == null){
			currentForks = forkService.getCurrentForks();
		}
		return currentForks;
	}

	public List<ForkInfo> getFilteredForkInfos() {
		return filteredForkInfos;
	}

	public void setFilteredForkInfos(List<ForkInfo> filteredForkInfos) {
		this.filteredForkInfos = filteredForkInfos;
	}
}
