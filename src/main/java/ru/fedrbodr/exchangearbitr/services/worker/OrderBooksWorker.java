package ru.fedrbodr.exchangearbitr.services.worker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.fedrbodr.exchangearbitr.dao.MarketPositionFastRepositoryCustom;
import ru.fedrbodr.exchangearbitr.dao.model.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.dao.model.MarketPositionFast;
import ru.fedrbodr.exchangearbitr.dao.model.MarketPositionFastPK;
import ru.fedrbodr.exchangearbitr.dao.model.SymbolPair;
import ru.fedrbodr.exchangearbitr.services.LimitOrderService;
import ru.fedrbodr.exchangearbitr.services.MarketPositionFastService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class OrderBooksWorker implements Runnable {
	public static final int RUN_MIN_PAUSE = 4000;

	private boolean doGrabbing = false;

	@Autowired
	private MarketPositionFastService marketPositionFastService;

	@Autowired
	private LimitOrderService orderService;
	@Autowired
	private MarketPositionFastRepositoryCustom marketPositionFastRepositoryCustom;

	private Date startPreviousCall;
	private int threadCount = 20;

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

	List<MarketPositionFastPK> alreadyLoadedOrdersBySymbolAndExchangeList;
	public void readAndSaveOrderBooksByTopMarketPositions() throws InterruptedException {
		Date start = new Date();
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		List<FutureTask<Void>> taskList = new ArrayList<>();
		/* Do not be surprised MarketPositionFastPK is suitable for check exchangeMeta and symbolPair uniqueness */
		alreadyLoadedOrdersBySymbolAndExchangeList = new ArrayList<>();

		List<Object[]> topMarketPositionDif = marketPositionFastRepositoryCustom.selectTopMarketPositionFastCompareList();
		for (Object[] marketPositionDif : topMarketPositionDif) {
			MarketPositionFast marketPositionFast =(MarketPositionFast) marketPositionDif[0];
			MarketPositionFast marketPositionFastToCompare =(MarketPositionFast) marketPositionDif[1];
			MarketPositionFast marketPositionBuy;
			MarketPositionFast marketPositionSell;
			if(marketPositionFast.getAskPrice().compareTo(marketPositionFastToCompare.getBidPrice()) > 1){
				marketPositionBuy = marketPositionFast;
				marketPositionSell = marketPositionFastToCompare;

			}else{
				marketPositionBuy = marketPositionFastToCompare;
				marketPositionSell = marketPositionFast;
			}
			addOrdersReaderFutureTaskToTaskList(executor, taskList, marketPositionBuy, marketPositionSell);
		}

		executor.shutdown();

		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		}

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

	private void addOrdersReaderFutureTaskToTaskList(ExecutorService executor, List<FutureTask<Void>> taskList, MarketPositionFast buyMarketPosition, MarketPositionFast sellMarketPosition) {
		SymbolPair symbolPair = buyMarketPosition.getMarketPositionFastPK().getSymbolPair();
		ExchangeMeta buyExchangeMeta = buyMarketPosition.getMarketPositionFastPK().getExchangeMeta();
		ExchangeMeta sellExchangeMeta = sellMarketPosition.getMarketPositionFastPK().getExchangeMeta();

		FutureTask<Void> exchangeReaderBuyExchangeTask = new FutureTask<>(() -> {
			return getaVoid(symbolPair, buyExchangeMeta);

		});

		FutureTask<Void> exchangeReaderSellExchangeTask = new FutureTask<>(() -> {
			return getaVoid(symbolPair, sellExchangeMeta);
		});

		taskList.add(exchangeReaderBuyExchangeTask);
		taskList.add(exchangeReaderSellExchangeTask);
		executor.execute(exchangeReaderBuyExchangeTask);
		executor.execute(exchangeReaderSellExchangeTask);

	}



	private Void getaVoid(SymbolPair symbolPair, ExchangeMeta exchangeMeta) {
		/* Do not be surprised MarketPositionFastPK is suitable for check exchangeMeta and symbolPair uniqueness */
		MarketPositionFastPK buyOrderBookUniqKey = new MarketPositionFastPK(exchangeMeta, symbolPair);

		if (!alreadyLoadedOrdersBySymbolAndExchangeList.contains(buyOrderBookUniqKey)) {
			/* save full order book for buy currency pair on the "buy" exchange, 100 rows*/
			/*orderService.readConvertCalcAndSaveUniOrders(symbolPair, exchangeMeta, "localhost", 8080);*/
			orderService.readConvertCalcAndSaveUniOrders(symbolPair, exchangeMeta, null, null);
			alreadyLoadedOrdersBySymbolAndExchangeList.add(buyOrderBookUniqKey);
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
