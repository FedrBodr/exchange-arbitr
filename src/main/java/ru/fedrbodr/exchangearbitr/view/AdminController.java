package ru.fedrbodr.exchangearbitr.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import ru.fedrbodr.exchangearbitr.services.worker.MarketsSummariesWorker;
import ru.fedrbodr.exchangearbitr.services.worker.OrderBooksWorker;

@Controller
public class AdminController {
	@Autowired
	private TaskExecutor taskExecutor;
	@Autowired
	private MarketsSummariesWorker marketsSummariesWorker;
	@Autowired
	private OrderBooksWorker orderBooksWorker;

	public String getMarketsSummariesCrawlingStatus(){
		return marketsSummariesWorker.isDoGrabbing()?"RUNNING":"STOPPED";
	}

	public String getOrderBooksCrawlingStatus(){
		return orderBooksWorker.isDoGrabbing()?"RUNNING":"STOPPED";
	}

	public void startAllGrabbing() {
		startMarketSummariesGrabbing();
		startOrderBooksGrabbing();
	}

	public void stopAllGrabbing() {
		marketsSummariesWorker.setDoGrabbing(false);
		orderBooksWorker.setDoGrabbing(false);
	}


	public void startMarketSummariesGrabbing() {
		marketsSummariesWorker.setDoGrabbing(true);
		taskExecutor.execute(marketsSummariesWorker);
	}

	public void stopMarketSummariesGrabbing() {
		marketsSummariesWorker.setDoGrabbing(false);
	}

	public void startOrderBooksGrabbing() {
		orderBooksWorker.setDoGrabbing(true);
		taskExecutor.execute(orderBooksWorker);
	}

	public void stopOrderBooksGrabbing() {
		orderBooksWorker.setDoGrabbing(false);
	}


}
