package ru.fedrbodr.exchangearbitr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.service.impl.BittrexExchangeWorkerImpl;

import java.io.IOException;
import java.util.Date;

@Service
public class CrawlerWorker {
	@Autowired
	private BittrexExchangeWorkerImpl bittrexExchangeWorker;

	public void do100DataSaves() throws InterruptedException {
		Date start = new Date();
		System.out.println("Before start 10 iteration of bittrexExchangeWorker.readAndSaveMarketPositions");
		Date startPreviousCall = new Date();
		for(int i = 100; i>0 ; i--) {
			try {
				bittrexExchangeWorker.readAndSaveMarketPositions();
			} catch (IOException e) {
				e.printStackTrace();
			}
			long lastCallWas = System.currentTimeMillis() - startPreviousCall.getTime();
			if( lastCallWas < 500){
				this.wait(500 - lastCallWas);
			}
			startPreviousCall = new Date();
		}
		Date end = new Date();

		System.out.println("After start 10 iteration of bittrexExchangeWorker.readAndSaveMarketPositions total time in " +
				"millisecconds:"+ (start.getTime() - end.getTime()));
	}
}
