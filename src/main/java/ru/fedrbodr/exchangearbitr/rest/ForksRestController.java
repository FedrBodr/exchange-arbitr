package ru.fedrbodr.exchangearbitr.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.fedrbodr.exchangearbitr.dao.longtime.reports.ForkInfo;
import ru.fedrbodr.exchangearbitr.rest.dto.ForkInfoDto;
import ru.fedrbodr.exchangearbitr.services.ForkService;

import java.util.LinkedList;
import java.util.List;

@RestController
public class ForksRestController {
	@Autowired
	private ForkService forkService;

	@RequestMapping(value = "/api/forks", method = RequestMethod.GET)
	public List<ForkInfoDto> getCurrentForks() {
		return convertToForkInfoDtoList(forkService.getCurrentForks());
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