package ru.fedrbodr.exchangearbitr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import ru.fedrbodr.exchangearbitr.service.CrawlerWorker;

@SpringBootApplication
public class ExchangeArbitrApplication {

	public static void main(String[] args) throws InterruptedException {

		ApplicationContext ctx = SpringApplication.run(ExchangeArbitrApplication.class, args);
		ctx.getBean(CrawlerWorker.class).do10DataSaves();
	}
}
