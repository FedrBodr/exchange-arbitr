package ru.fedrbodr.exchangearbitr.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.dao.ExchangeRepository;
import ru.fedrbodr.exchangearbitr.model.dao.ExchangeMeta;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

@Service
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
	@Autowired
	private ExchangeRepository exchangeRepository;
	private boolean doGrabbing = false;

	@PostConstruct
	private void init() throws NoSuchFieldException, IllegalAccessException {
		/* TODO move preinit to more convenient place ? */
		exchangeRepository.save(ExchangeMeta.BITTREX);
		exchangeRepository.save(ExchangeMeta.COINEXCHANGE);
		exchangeRepository.save(ExchangeMeta.POLONIEX);
		exchangeRepository.save(ExchangeMeta.BINANCE);
		exchangeRepository.flush();
	}

	@Override
	public void run() {
		Date start = new Date();
		log.info("Before start NonStop iteration readAllExchangeSummaries of bittrexExchangeReader.readAndSaveMarketPositionsBySummaries");
		Date startPreviousCall = new Date();
		int threadNum = Runtime.getRuntime().availableProcessors()-1;

		while(doGrabbing) {
			try {
				startPreviousCall = readAllExchangeSummaries(startPreviousCall, threadNum);
			} catch (InterruptedException e) {
				log.error("Maybe int arror on shut down and it is ok" + e.getMessage(), e);
			}
		}

		log.info("After stop NonStop iteration readAllExchangeSummaries total time in  millisecconds: []", (start.getTime() - new Date().getTime()));
	}




	private Date readAllExchangeSummaries(Date startPreviousCall, int threadCount) throws InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		List<FutureTask<Void>> taskList = new ArrayList<>();

		addReadedTaskFutureTaskToTaskList(executor, taskList, bittrexExchangeReader);
		addReadedTaskFutureTaskToTaskList(executor, taskList, poloniexExchangeReader);
		addReadedTaskFutureTaskToTaskList(executor, taskList, coinexchangeExchangeReader);
		long lastCallWas = System.currentTimeMillis() - startPreviousCall.getTime();
		if( lastCallWas < BINANCE_ALL_TICKERS_PAUSE){
			addReadedTaskFutureTaskToTaskList(executor, taskList, binanceExchangeReader);
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
