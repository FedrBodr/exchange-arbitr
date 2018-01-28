package ru.fedrbodr.exchangearbitr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import ru.fedrbodr.exchangearbitr.service.CrawlerWorker;

@SpringBootApplication
public class ExchangeArbitrApplication {

	public static void main(String[] args) {

		ApplicationContext ctx = SpringApplication.run(ExchangeArbitrApplication.class, args);
		try {
			ctx.getBean(CrawlerWorker.class).do100DataSaves();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
