package ru.fedrbodr.exchangearbitr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

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
		for(int i = 10; i>0 ; i--) {
			try {
				bittrexExchangeReader.readAndSaveMarketPositions();
				poloniexExchangeReader.readAndSaveMarketPositions();
			} catch (IOException e) {
				e.printStackTrace();
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
}
