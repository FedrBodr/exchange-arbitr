package ru.fedrbodr.exchangearbitr.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import ru.fedrbodr.exchangearbitr.services.worker.MarketsSummariesWorker;
import ru.fedrbodr.exchangearbitr.services.worker.OrderBooksWorker;

import java.util.concurrent.ExecutorService;

@Controller
public class AdminController {
	@Autowired
	@Qualifier("marketSumTaskExecutor")
	private ExecutorService marketSummTaskExecutor;
	@Autowired
	@Qualifier("ordersTaskExecutor")
	private ExecutorService ordersTaskExecutor;
	@Autowired
	private MarketsSummariesWorker marketsSummariesWorker;
	@Autowired
	private OrderBooksWorker orderBooksWorker;

	@Secured("ADMIN")
	public String getMarketsSummariesCrawlingStatus() {
		return marketsSummariesWorker.isDoGrabbing() ? "RUNNING" : "STOPPED";
	}

	@Secured("ADMIN")
	public String getOrderBooksCrawlingStatus() {
		return orderBooksWorker.isDoGrabbing() ? "RUNNING" : "STOPPED";
	}

	@Secured("ADMIN")
	public void startAllGrabbing() {
		startMarketSummariesGrabbing();
		startOrderBooksGrabbing();
	}

	@Secured("ADMIN")
	public void startMarketSummariesGrabbing() {
		marketsSummariesWorker.setDoGrabbing(true);
		marketSummTaskExecutor.execute(marketsSummariesWorker);
	}

	@Secured("ADMIN")
	public void startOrderBooksGrabbing() {
		orderBooksWorker.setDoGrabbing(true);
		ordersTaskExecutor.execute(orderBooksWorker);
	}

	@Secured("ADMIN")
	public void stopAllGrabbing() {
		marketsSummariesWorker.setDoGrabbing(false);
		orderBooksWorker.setDoGrabbing(false);
	}

	@Secured("ADMIN")
	public void stopMarketSummariesGrabbing() {
		marketsSummariesWorker.setDoGrabbing(false);
	}

	@Secured("ADMIN")
	public void stopOrderBooksGrabbing() {
		orderBooksWorker.setDoGrabbing(false);
	}
}
