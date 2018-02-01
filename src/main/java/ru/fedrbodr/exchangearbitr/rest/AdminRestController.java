package ru.fedrbodr.exchangearbitr.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.fedrbodr.exchangearbitr.services.CrawlerWorker;

@RestController
@Slf4j
public class AdminRestController {

	@Autowired
	private CrawlerWorker crawlerWorker;
	@Autowired
	private TaskExecutor taskExecutor;
	@RequestMapping("/hello")
	public String hello() {
		return "Hello crypto world";
	}

	@RequestMapping("/start-grabbing")
	public String startGrabbing() {
		crawlerWorker.setDoGrabbing(true);
		taskExecutor.execute(crawlerWorker);
		return "ok";
	}

	@RequestMapping("/stop-grabbing")
	public String stopGrabbing() {
		crawlerWorker.setDoGrabbing(false);
		return "ok";
	}

}
