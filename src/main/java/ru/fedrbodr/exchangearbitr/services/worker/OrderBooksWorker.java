package ru.fedrbodr.exchangearbitr.services.worker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.fedrbodr.exchangearbitr.dao.model.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.dao.model.MarketPositionFastPK;
import ru.fedrbodr.exchangearbitr.dao.model.SymbolPair;
import ru.fedrbodr.exchangearbitr.model.MarketPositionFastCompare;
import ru.fedrbodr.exchangearbitr.services.LimitOrderService;
import ru.fedrbodr.exchangearbitr.services.MarketPositionFastService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class OrderBooksWorker implements Runnable {
	public static final int RUN_MIN_PAUSE = 4000;

	private boolean doGrabbing = false;

	@Autowired
	private MarketPositionFastService marketPositionFastService;

	@Autowired
	private LimitOrderService orderService;

	private Date startPreviousCall;

	public OrderBooksWorker() {
		startPreviousCall = new Date();
	}

	@Override
	public void run() {
		Date start = new Date();
		log.info("Before start OrderBooksWorker.run");
		int threadNum = Runtime.getRuntime().availableProcessors() - 1;

		while (doGrabbing) {
			try {
				readAndSaveOrderBooksByTopMarketPositions(threadNum);
			} catch (InterruptedException e) {
				log.error("Maybe it error on shut down and it is ok ?" + e.getMessage(), e);
			}
		}

		log.info("After stop OrderBooksWorker.run total time in  seconds: {}", (new Date().getTime() - start.getTime()) / 1000);
	}

	public void readAndSaveOrderBooksByTopMarketPositions(int threadCount) throws InterruptedException {
 		Date start = new Date();
		log.info("Before start readAndSaveOrderBooksByTopMarketPositions");
		List<MarketPositionFastCompare> marketPositionCompareList = marketPositionFastService.getTop30FullMarketPositionFastCompareList();
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		List<FutureTask<Void>> taskList = new ArrayList<>();

		for (MarketPositionFastCompare marketPositionCompare : marketPositionCompareList) {
			addOrdersReaderFutureTaskToTaskList(executor, taskList, marketPositionCompare);
		}

		executor.shutdown();

		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		}

		long lastCallWas = System.currentTimeMillis() - startPreviousCall.getTime();
		if (lastCallWas < RUN_MIN_PAUSE) {
			synchronized (this) { // obtain lock's monitor
				this.wait(RUN_MIN_PAUSE - lastCallWas);
			}
		}
		startPreviousCall = new Date();

		log.info("After stop readAndSaveOrderBooksByTopMarketPositions total time in  seconds: {}", (new Date().getTime() - start.getTime()) / 1000);
	}

	List<MarketPositionFastPK> alreadyLoadedOrdersBySymbolAndExchangeList;
	private void addOrdersReaderFutureTaskToTaskList(ExecutorService executor, List<FutureTask<Void>> taskList, MarketPositionFastCompare marketPositionFastCompare) {
		SymbolPair symbolPair = marketPositionFastCompare.getBuyMarketPosition().getMarketPositionFastPK().getSymbolPair();
		ExchangeMeta buyExchangeMeta = marketPositionFastCompare.getBuyMarketPosition().getMarketPositionFastPK().getExchangeMeta();
		ExchangeMeta sellExchangeMeta = marketPositionFastCompare.getSellMarketPosition().getMarketPositionFastPK().getExchangeMeta();
		/* Do not be surprised MarketPositionFastPK is suitable for check exchangeMeta and symbolPair uniqueness */
		alreadyLoadedOrdersBySymbolAndExchangeList = new ArrayList<>();


		FutureTask<Void> exchangeReaderTask = new FutureTask<>(() -> {
			/* Do not be surprised MarketPositionFastPK is suitable for check exchangeMeta and symbolPair uniqueness */
			MarketPositionFastPK buyOrderBookUniqKey = new MarketPositionFastPK(buyExchangeMeta, symbolPair);
			MarketPositionFastPK sellOrderBookUniqKey = new MarketPositionFastPK(sellExchangeMeta, symbolPair);

			if (!alreadyLoadedOrdersBySymbolAndExchangeList.contains(buyOrderBookUniqKey)) {
				/* save full order book for buy currency pair on the "buy" exchange, 100 rows*/
				orderService.readConvertCalcAndSaveUniOrders(symbolPair, buyExchangeMeta);
				alreadyLoadedOrdersBySymbolAndExchangeList.add(buyOrderBookUniqKey);
			}

			if (!alreadyLoadedOrdersBySymbolAndExchangeList.contains(sellOrderBookUniqKey)) {
				/* save full order book for sell currency pair on the "sel" exchange, 100 rows*/
				orderService.readConvertCalcAndSaveUniOrders(symbolPair, sellExchangeMeta);
				alreadyLoadedOrdersBySymbolAndExchangeList.add(sellOrderBookUniqKey);
			}
			return null;
		});
		taskList.add(exchangeReaderTask);
		executor.execute(exchangeReaderTask);
	}

	public void setDoGrabbing(boolean doGrabbing) {
		this.doGrabbing = doGrabbing;
	}

	public boolean isDoGrabbing() {
		return doGrabbing;
	}
}
