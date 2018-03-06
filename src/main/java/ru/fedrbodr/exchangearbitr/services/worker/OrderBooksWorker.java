package ru.fedrbodr.exchangearbitr.services.worker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.MarketPositionFast;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.Symbol;
import ru.fedrbodr.exchangearbitr.dao.shorttime.repo.ExchangeMetaRepository;
import ru.fedrbodr.exchangearbitr.dao.shorttime.repo.MarketPositionFastRepositoryCustom;
import ru.fedrbodr.exchangearbitr.services.ForkService;
import ru.fedrbodr.exchangearbitr.services.LimitOrderService;
import ru.fedrbodr.exchangearbitr.services.MarketPositionFastService;
import ru.fedrbodr.exchangearbitr.services.SymbolService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

@Component
@Slf4j
public class OrderBooksWorker implements Runnable {
	public static final int RUN_MIN_PAUSE = 9000;

	private boolean doGrabbing = false;

	@Autowired
	private ForkService forkService;

	@Autowired
	private LimitOrderService orderService;
	@Autowired
	private MarketPositionFastRepositoryCustom marketPositionFastRepositoryCustom;
	@Autowired
	private MarketPositionFastService marketPositionFastService;
	@Autowired
	private SymbolService symbolService;
	@Autowired
	private ExchangeMetaRepository exchangeMetaRepository;

	private Date startPreviousCall;
	private int threadCount = 40;

	public OrderBooksWorker() {
		startPreviousCall = new Date();
	}

	@Override
	public void run() {
		Date start = new Date();
		log.info("Before start OrderBooksWorker.run");
		while (doGrabbing) {
			try {
				readAndSaveOrderBooksByTopMarketPositions();
			} catch (InterruptedException e) {
				log.error("Maybe it error on shut down and it is ok ?" + e.getMessage(), e);
			}
		}

		log.info("After stop OrderBooksWorker.run total time in  seconds: {}", (new Date().getTime() - start.getTime()) / 1000);
	}

	public void readAndSaveOrderBooksByTopMarketPositions() throws InterruptedException {
		Date start = new Date();
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		List<FutureTask<Void>> taskList = new ArrayList<>();

		/* Do not be surprised MarketPositionFastPK is suitable for check exchangeMeta and symbol uniqueness */
		Set<MarketPositionFast> marketPositionFastSet = marketPositionFastService.getMarketPositionSetToLoadOrderBooks();
		for (MarketPositionFast marketPositionFast : marketPositionFastSet) {
			addOrderBookReaderFutureTaskToTaskList(executor, taskList, marketPositionFast);
		}
		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		}
		long allOrdersLoadingTime = (new Date().getTime() - start.getTime());
		log.info("After all orders Load seconds: {}", allOrdersLoadingTime / 1000);
		/* Start forks calculating for saving statistics */
		forkService.determineAndPersistForks(allOrdersLoadingTime);
		/*call preinit last forks cache*/
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		Callable<Void> tCallable = () -> {
			/* just for pre init last forks cache */
			forkService.getCurrentForks();
			return null;
		};
		FutureTask futureTask = new FutureTask(tCallable);
		executorService.execute(futureTask);

		long lastCallWas = System.currentTimeMillis() - startPreviousCall.getTime();
		if (lastCallWas < RUN_MIN_PAUSE) {
			synchronized (this) { // obtain lock's monitor
				this.wait(RUN_MIN_PAUSE - lastCallWas);
			}
		}
		startPreviousCall = new Date();

		long lastOrderLoadingTimeMillis = (new Date().getTime() - start.getTime());
		log.info("After stop readAndSaveOrderBooksByTopMarketPositions total time in  seconds: {}", lastOrderLoadingTimeMillis / 1000);
	}

	private void addOrderBookReaderFutureTaskToTaskList(ExecutorService executorForSynk, List<FutureTask<Void>> taskList, MarketPositionFast marketPosition) {
		Symbol symbol = marketPosition.getMarketPositionFastPK().getSymbol();
		ExchangeMeta exchangeMeta = marketPosition.getMarketPositionFastPK().getExchangeMeta();

		FutureTask<Void> exchangeReaderBuyExchangeTask = new FutureTask<>(() -> {
			return getaVoid(symbol, exchangeMeta);
		});
		taskList.add(exchangeReaderBuyExchangeTask);
		executorForSynk.submit(exchangeReaderBuyExchangeTask);
	}

	private Void getaVoid(Symbol symbol, ExchangeMeta exchangeMeta) {
		try {
			orderService.readConvertCalcAndSaveUniOrders(symbol, exchangeMeta);
		} catch (InterruptedException e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public void setDoGrabbing(boolean doGrabbing) {
		this.doGrabbing = doGrabbing;
	}

	public boolean isDoGrabbing() {
		return doGrabbing;
	}
}
