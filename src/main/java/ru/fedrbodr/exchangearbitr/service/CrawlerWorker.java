package ru.fedrbodr.exchangearbitr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.service.impl.BittrexExchangeWorkerImpl;

import java.io.IOException;
import java.util.Date;

@Service
public class CrawlerWorker {
	@Autowired
	private BittrexExchangeWorkerImpl bittrexExchangeWorker;

	public void do10DataSaves() {
		Date start = new Date();
		System.out.println("Before start 10 iteration of bittrexExchangeWorker.readAndSaveMarketPositions");
		for(int i = 10; i>0; i--) {
			try {
				bittrexExchangeWorker.readAndSaveMarketPositions();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Date end = new Date();

		System.out.println("After start 10 iteration of bittrexExchangeWorker.readAndSaveMarketPositions total time in " +
				"millisecconds:"+ (start.getTime() - end.getTime()));
	}
}
