package ru.fedrbodr.exchangearbitr.various;

import org.junit.Test;

import java.util.concurrent.*;

public class ExecutorTests {
	@Test
	public void executorTest() throws InterruptedException, ExecutionException {
		ExecutorService executorService = Executors.newSingleThreadExecutor();

		Callable<Void> tCallable = new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				Thread.sleep(1000);
				System.out.println("Doing thread complete");
				return null;
			}
		};
		FutureTask futureTask = new FutureTask(tCallable);
		executorService.submit(futureTask);

		System.out.println("Doing useful work");
		Thread.sleep(2000);
		System.out.println("afterr Doing useful work");
	}
}
