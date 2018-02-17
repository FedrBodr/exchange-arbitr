package ru.fedrbodr.exchangearbitr.services.worker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.fedrbodr.exchangearbitr.dao.model.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.services.ExchangeReader;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class MarketsSummariesWorker implements Runnable {
	public static final int REQUESTS_PAUSE = 2000;
	public static final int BINANCE_ALL_TICKERS_PAUSE = 20000;
	private boolean doGrabbing = false;
	private Date startPreviousCall;
	private Date startPreviousBitrixCall;
	@Autowired
	private Map<ExchangeMeta, ExchangeReader> exchangeMetaToExchangeSummariesReaderMap;
	@Autowired
	@Qualifier("binanceExchangeReaderImpl")
	private ExchangeReader binanceExchangeReader;

	public MarketsSummariesWorker() {
		startPreviousCall = new Date();
		startPreviousBitrixCall = new Date();
	}

	@Override
	public void run() {
		Date start = new Date();
		log.info("Before start run iteration MarketsSummariesWorker");
		int threadNum = Runtime.getRuntime().availableProcessors()-1;

		while(doGrabbing) {
			try {
				readAllExchangeSummaries(threadNum);
			} catch (InterruptedException e) {
				log.error("Maybe int arror on shut down and it is ok" + e.getMessage(), e);
			}
		}

		log.info("After stop run iteration MarketsSummariesWorker total time in  millisecconds: {}", (start.getTime() - new Date().getTime()));
	}




	private void readAllExchangeSummaries(int threadCount) throws InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		List<FutureTask<Void>> taskList = new ArrayList<>();

		Collection<ExchangeReader> values = exchangeMetaToExchangeSummariesReaderMap.values();
		for (ExchangeReader value : values) {
			addReaderTaskFutureTaskToTaskList(executor, taskList, value);
		}

		/*TODO refactor this to configarable limits for each exchange
		* and start wright wiki or project documentation*/
		long lastCallWas = System.currentTimeMillis() - startPreviousBitrixCall.getTime();
		if( lastCallWas > BINANCE_ALL_TICKERS_PAUSE){
			addReaderTaskFutureTaskToTaskList(executor, taskList, binanceExchangeReader);
			startPreviousBitrixCall = new Date();
		}

		executor.shutdown();

		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		}

		lastCallWas = System.currentTimeMillis() - startPreviousCall.getTime();
		if( lastCallWas < REQUESTS_PAUSE){
			synchronized (this) { // obtain lock's monitor
				this.wait(REQUESTS_PAUSE - lastCallWas);
			}
		}
		startPreviousCall = new Date();
	}


	private void addReaderTaskFutureTaskToTaskList(ExecutorService executor, List<FutureTask<Void>> taskList, ExchangeReader exchangeReader) {
		FutureTask<Void> exchangeReaderTask = new FutureTask<>(() -> {
			try {
				exchangeReader.readAndSaveMarketPositionsBySummaries();
			} catch (IOException e) {
				log.error("Error occurred while readAndSaveMarketPositionsBySummaries ", e);
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
