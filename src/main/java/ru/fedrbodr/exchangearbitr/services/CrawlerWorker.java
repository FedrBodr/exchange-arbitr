package ru.fedrbodr.exchangearbitr.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CrawlerWorker implements Runnable {
	public static final int REQUESTS_PAUSE = 2000;
	public static final int BINANCE_ALL_TICKERS_PAUSE = 15000;
	@Autowired
	@Qualifier("bittrexExchangeReaderImpl")
	private ExchangeReader bittrexExchangeReader;
	@Autowired
	@Qualifier("poloniexExchangeReaderImpl")
	private ExchangeReader poloniexExchangeReader;
	@Autowired
	@Qualifier("binanceExchangeReaderImpl")
	private ExchangeReader binanceExchangeReader;
	@Autowired
	@Qualifier("coinexchangeExchangeReaderImpl")
	private ExchangeReader coinexchangeExchangeReader;
	private boolean doGrabbing = false;
	private Date startPreviousCall;
	private Date startPreviousBitrixCall;

	@Override
	public void run() {
		Date start = new Date();
		log.info("Before start NonStop iteration readAllExchangeSummaries of bittrexExchangeReader.readAndSaveMarketPositionsBySummaries");
		startPreviousCall = new Date();
		startPreviousBitrixCall = new Date();
		int threadNum = Runtime.getRuntime().availableProcessors()-1;

		while(doGrabbing) {
			try {
				startPreviousCall = readAllExchangeSummaries(threadNum);
			} catch (InterruptedException e) {
				log.error("Maybe int arror on shut down and it is ok" + e.getMessage(), e);
			}
		}

		log.info("After stop NonStop iteration readAllExchangeSummaries total time in  millisecconds: []", (start.getTime() - new Date().getTime()));
	}




	private Date readAllExchangeSummaries(int threadCount) throws InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		List<FutureTask<Void>> taskList = new ArrayList<>();

		addReadedTaskFutureTaskToTaskList(executor, taskList, bittrexExchangeReader);
		addReadedTaskFutureTaskToTaskList(executor, taskList, poloniexExchangeReader);
		addReadedTaskFutureTaskToTaskList(executor, taskList, coinexchangeExchangeReader);
		/*TODO refactor this to configarable limits for each exchange
		* and start wright wiki or project documentation*/
		long lastCallWas = System.currentTimeMillis() - startPreviousBitrixCall.getTime();
		if( lastCallWas > BINANCE_ALL_TICKERS_PAUSE){
			log.info("Call addReadedTaskFutureTaskToTaskList for binanceExchangeReader");
			addReadedTaskFutureTaskToTaskList(executor, taskList, binanceExchangeReader);
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
				// Release the lock, but wait only 500 ms for notify
				this.wait(REQUESTS_PAUSE - lastCallWas);
			}
		}
		startPreviousCall = new Date();
		return startPreviousCall;
	}


	private void addReadedTaskFutureTaskToTaskList(ExecutorService executor, List<FutureTask<Void>> taskList, ExchangeReader exchangeReader) {
		FutureTask<Void> bittrexReaderTask = new FutureTask<>(() -> {
			try {
				exchangeReader.readAndSaveMarketPositionsBySummaries();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		});
		taskList.add(bittrexReaderTask);
		executor.execute(bittrexReaderTask);
	}

	public void setDoGrabbing(boolean doGrabbing) {
		this.doGrabbing = doGrabbing;
	}

	public boolean isDoGrabbing() {
		return doGrabbing;
	}
}
