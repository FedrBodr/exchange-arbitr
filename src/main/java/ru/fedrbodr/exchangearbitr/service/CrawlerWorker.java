package ru.fedrbodr.exchangearbitr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

@Service
public class CrawlerWorker {
	@Autowired
	@Qualifier("bittrexExchangeReaderImpl")
	private ExchangeReader bittrexExchangeReader;
	@Autowired
	@Qualifier("poloniexExchangeReaderImpl")
	private ExchangeReader poloniexExchangeReader;

	public void do10DataSaves() throws InterruptedException {
		Date start = new Date();
		System.out.println("Before start 10 iteration of bittrexExchangeReader.readAndSaveMarketPositions");
		Date startPreviousCall = new Date();
		int threadNum = Runtime.getRuntime().availableProcessors()-1;

		for(int i = 10; i>0 ; i--) {
			ExecutorService executor = Executors.newFixedThreadPool(threadNum);
			List<FutureTask<Void>> taskList = new ArrayList<>();

			FutureTask<Void> bittrexReaderTask = getVoidFutureTask(bittrexExchangeReader);
			taskList.add(bittrexReaderTask);
			executor.execute(bittrexReaderTask);

			FutureTask<Void> poloniexReaderTask = getVoidFutureTask(poloniexExchangeReader);
			taskList.add(poloniexReaderTask);
			executor.execute(poloniexReaderTask);

			executor.shutdown();

			try {
				executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			} catch (InterruptedException e) {
				System.out.println(e);
			}

			long lastCallWas = System.currentTimeMillis() - startPreviousCall.getTime();
			if( lastCallWas < 500){
				synchronized (this) { // obtain lock's monitor
					// Release the lock, but wait only 500 ms for notify
					this.wait(500 - lastCallWas);
				}
			}
			startPreviousCall = new Date();
		}
		Date end = new Date();

		System.out.println("After start 10 iteration of bittrexExchangeReader.readAndSaveMarketPositions total time in " +
				"millisecconds:"+ (start.getTime() - end.getTime()));
	}

	private FutureTask<Void> getVoidFutureTask(ExchangeReader reader) {
		return new FutureTask<>(() -> {
					try {
						reader.readAndSaveMarketPositions();
					} catch (IOException e) {
						e.printStackTrace();
					}
					return null;
				});
	}
}
