package ru.fedrbodr.exchangearbitr.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.fedrbodr.exchangearbitr.dao.longtime.reports.ForkInfo;
import ru.fedrbodr.exchangearbitr.rest.dto.ForkInfoDto;
import ru.fedrbodr.exchangearbitr.services.ForkService;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

@RestController
public class ForksRestController {
	@Autowired
	private ForkService forkService;

	@RequestMapping(value = "/api/forks", method = RequestMethod.GET)
	public List<ForkInfoDto> getCurrentForks() {
		List<ForkInfo> currentForks = forkService.getCurrentForks();
		List<ForkInfo> filteredForks = new LinkedList<>();
		for (ForkInfo fork : currentForks) {
			if(fork.getProfits().size() > 0 && fork.getProfits().get(0).getProfit().multiply(BigDecimal.valueOf(100)).compareTo(BigDecimal.valueOf(0.54)) < 0){
				/* hide small profit fork - its used by self for auto raiding */
			}else if(fork.getProfits().size() > 1 && fork.getProfits().get(1).getProfit().multiply(BigDecimal.valueOf(100)).compareTo(BigDecimal.valueOf(11))>0 ||
					fork.getProfits().size() > 0 && fork.getProfits().get(0).getProfit().multiply(BigDecimal.valueOf(100)).compareTo(BigDecimal.valueOf(11))>0){
				// ignore unreal forks for bot
			}else{
				filteredForks.add(fork);
			}

		}
		return convertToForkInfoDtoList(filteredForks);
	}

	private List<ForkInfoDto> convertToForkInfoDtoList(List<ForkInfo> currentForks) {
		List<ForkInfoDto> forkInfoDtos = new LinkedList<>();
		for (ForkInfo currentFork : currentForks) {
			ForkInfoDto forkInfoDto = new ForkInfoDto(currentFork);
			forkInfoDtos.add(forkInfoDto);
		}

		return forkInfoDtos;
	}
}