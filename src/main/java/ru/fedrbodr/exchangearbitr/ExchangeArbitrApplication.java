package ru.fedrbodr.exchangearbitr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import ru.fedrbodr.exchangearbitr.services.CrawlerWorker;

@SpringBootApplication
@EnableCaching
public class ExchangeArbitrApplication {

	public static void main(String[] args) throws InterruptedException {

		ApplicationContext ctx = SpringApplication.run(ExchangeArbitrApplication.class, args);
		//ctx.getBean(CrawlerWorker.class).do10DataSaves();
		ctx.getBean(CrawlerWorker.class).doNonStop();

	}
}
