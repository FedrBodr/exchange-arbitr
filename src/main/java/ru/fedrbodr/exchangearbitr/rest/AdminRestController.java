package ru.fedrbodr.exchangearbitr.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.fedrbodr.exchangearbitr.services.worker.MarketsSummariesWorker;

@RestController
@Slf4j
public class AdminRestController {

	@Autowired
	private MarketsSummariesWorker marketsSummariesWorker;
	@Autowired
	private TaskExecutor taskExecutor;
	@RequestMapping("/hello")
	public String hello() {
		return "Hello crypto world";
	}

	@RequestMapping("/start-grabbing")
	@Secured("ADMIN")
	public String startGrabbing() {
		marketsSummariesWorker.setDoGrabbing(true);
		taskExecutor.execute(marketsSummariesWorker);
		return "ok";
	}

	@RequestMapping("/stop-grabbing")
	@Secured("ADMIN")
	public String stopGrabbing() {
		marketsSummariesWorker.setDoGrabbing(false);
		return "ok";
	}

}
