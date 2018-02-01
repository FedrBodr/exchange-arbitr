package ru.fedrbodr.exchangearbitr.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import ru.fedrbodr.exchangearbitr.services.CrawlerWorker;

import javax.faces.event.ActionEvent;

@Controller
public class AdminController {
	@Autowired
	private TaskExecutor taskExecutor;
	@Autowired
	private CrawlerWorker crawlerWorker;

	public String getCrawlingStatus(){
		return crawlerWorker.isDoGrabbing()?"RUNNING":"STOPPED";
	}

	public void startGrabbing(ActionEvent e) {
		crawlerWorker.setDoGrabbing(true);
		taskExecutor.execute(crawlerWorker);
	}

	public void stopGrabbing() {
		crawlerWorker.setDoGrabbing(false);
	}
}
