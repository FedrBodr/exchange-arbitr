package ru.fedrbodr.exchangearbitr.services.worker;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.MarketPositionFast;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.Symbol;
import ru.fedrbodr.exchangearbitr.services.ForkService;
import ru.fedrbodr.exchangearbitr.services.LimitOrderService;
import ru.fedrbodr.exchangearbitr.services.MarketPositionFastService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

@Component
@Slf4j
public class OrderBooksWorker implements Runnable {
	public static final int RUN_MIN_PAUSE = 10000;

	private boolean doGrabbing = false;

	@Autowired
	private ForkService forkService;

	@Autowired
	private LimitOrderService orderService;
	@Autowired
	private MarketPositionFastService marketPositionFastService;

	private Date startPreviousCall;
	private int orderBookLoaderThreadCount = 100;

	public OrderBooksWorker() {
		startPreviousCall = new Date();
	}

	@Override
	public void run() {
		Date start = new Date();
		log.info("Before start OrderBooksWorker.run");
		while (doGrabbing) {
			try {
				readAndSaveOrderBooksByTopMarketPositions();
			} catch (InterruptedException e) {
				log.error("Maybe it error on shut down and it is ok ?" + e.getMessage(), e);
			}
		}

		log.info("After stop OrderBooksWorker.run total time in  seconds: {}", (new Date().getTime() - start.getTime()) / 1000);
	}

	public void readAndSaveOrderBooksByTopMarketPositions() throws InterruptedException {
		StopWatch stopWatchAll = StopWatch.createStarted();
		StopWatch stopWatch = StopWatch.createStarted();
		ExecutorService executor = Executors.newFixedThreadPool(orderBookLoaderThreadCount);
		List<FutureTask<Void>> taskList = new ArrayList<>();

		/* Do not be surprised MarketPositionFastPK is suitable for check exchangeMeta and symbol uniqueness */
		Set<MarketPositionFast> marketPositionFastSet = marketPositionFastService.getMarketPositionSetToLoadOrderBooks();
		for (MarketPositionFast marketPositionFast : marketPositionFastSet) {
			addOrderBookReaderFutureTaskToTaskList(executor, taskList, marketPositionFast);
		}
		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		}
		log.info("After all orders Load seconds: {}", stopWatch.getTime() / 1000);
		stopWatch.reset();
		/* Start forks calculating for saving statistics */
		forkService.determineAndPersistForks(stopWatch.getTime());
		log.info("After determineAndPersistForks: {} ms", stopWatch.getTime());
		stopWatch.reset();
		/*call preinit last forks cache*/
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		Callable<Void> tCallable = () -> {
			/* just for pre init last forks cache */
			forkService.getCurrentForks();
			return null;
		};
		FutureTask futureTask = new FutureTask(tCallable);
		executorService.execute(futureTask);

		orderService.deleteAll();
		log.info("After orderService.deleteAll(): {} ms", stopWatch.getTime());
		long lastCallWas = System.currentTimeMillis() - startPreviousCall.getTime();
		if (lastCallWas < RUN_MIN_PAUSE) {
			synchronized (this) { // obtain lock's monitor
				this.wait(RUN_MIN_PAUSE - lastCallWas);
			}
		}
		startPreviousCall = new Date();

		log.info("After stop readAndSaveOrderBooksByTopMarketPositions total time in  seconds: {}", stopWatchAll.getTime() / 1000);
	}

	private void addOrderBookReaderFutureTaskToTaskList(ExecutorService executorForSynk, List<FutureTask<Void>> taskList, MarketPositionFast marketPosition) {
		Symbol symbol = marketPosition.getMarketPositionFastPK().getSymbol();
		ExchangeMeta exchangeMeta = marketPosition.getMarketPositionFastPK().getExchangeMeta();

		FutureTask<Void> exchangeReaderBuyExchangeTask = new FutureTask<>(() -> getaVoid(symbol, exchangeMeta));
		taskList.add(exchangeReaderBuyExchangeTask);
		executorForSynk.submit(exchangeReaderBuyExchangeTask);
	}

	private Void getaVoid(Symbol symbol, ExchangeMeta exchangeMeta) {
		try {
			orderService.readConvertCalcAndSaveUniOrders(symbol, exchangeMeta);
		} catch (InterruptedException e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public void setDoGrabbing(boolean doGrabbing) {
		this.doGrabbing = doGrabbing;
	}

	public boolean isDoGrabbing() {
		return doGrabbing;
	}
}
