package ru.fedrbodr.huckster;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.PredicateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.hitbtc.v2.HitbtcExchange;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Slf4j
public class GetOrderBooksTests {
	public static final String BASE_SYMBOL = "STORM";
	public static final String COUNTER_SYMBOL = "BTC";
	public static final String MIN_PROFIT_TO_TRADE = "0.33";
	BigDecimal deposit = BigDecimal.valueOf(0.01);
	int coinsAmountForTradeOperation = 400;

	@Test
	public void putOrderHitBtcOrderBookTest() throws InterruptedException, ExecutionException {
		Exchange binanceBtcExchange = getExchange(BinanceExchange.class, "luG38Uo3teJBuvtLg0pHwXMbufQel2saTMnbeR4BHCJqFh3m70jQ7qYKzO6k7AvL",
				"RN1sjrpNhUGxo1GlMqtbNl4om86XwfFmIhKZX2U9ondYQXk798NVG6KfTPu7pRpw");
		Exchange hitBtcExchange = getExchange(HitbtcExchange.class, "d3cabfa6e0cb6e6cb0059ac44756a44b",
				"70c502446a0420f583ba2ee12abadef4");
//		Exchange kucoinExchange = getExchange(KucoinExchange.class, "5ac624d51e789d54952765ae", "eff807ac-c14f-4891-9b4d-3283085621c1");
//		Exchange cryptopiaExchange = getExchange(CryptopiaExchange.class);
//		Exchange livecoinExchange = getExchange(LivecoinExchange.class);

		CurrencyPair currencyPair = new CurrencyPair(BASE_SYMBOL, COUNTER_SYMBOL);
		Exchange firtsExchange = binanceBtcExchange;
		Exchange secondExchange = hitBtcExchange;
		try {
			Map<String, Wallet> secondExchangeWallets = secondExchange.getAccountService().getAccountInfo().getWallets();
			Wallet mainWallet = secondExchangeWallets.get("Main");
			Wallet tradingWallet = secondExchangeWallets.get("Trading");
			log.info("SecondExchange {} base wallet name is {} balance {} counter wallet name is {} balance {}",
					secondExchange.getExchangeSpecification().getExchangeClassName(), BASE_SYMBOL, tradingWallet.getBalance(new Currency(BASE_SYMBOL)),
					COUNTER_SYMBOL, tradingWallet.getBalance(new Currency(COUNTER_SYMBOL)));
			///firtsExchange.getTradeService().getTradeHistory()
/*			Map<String, Wallet> seccondExchangeWallets = secondExchange.getAccountService().getAccountInfo().getWallets();
			Wallet stormCounterWallet = firstExchangeWallets.get("STORM");
			log.info("base wallet name is " + baseWallet.getName() + " stormWallet toString " + baseWallet.toString());
			log.info(stormCounterWallet.getName() + "stormWallet toString " + stormCounterWallet.toString());*/
		} catch (IOException e) {
			log.error("ERROR occurred when  try to obtain balances");
		}
		Date allStarDate;
		while (true) {
			allStarDate = new Date();
//			log.info("Start load circle {}", allStarDate);
			OrderBook firtMarketOrderBook = null;
			OrderBook secondMarketOrderBook = null;

			ExecutorService loadOrderBookTaskFirstExchangeExecutor = Executors.newFixedThreadPool(2);

			Future<OrderBook> firstMarketOrderBookFuture = loadOrderBookTaskFirstExchangeExecutor.submit(getOrderBookCallable(currencyPair, firtsExchange));
			Future<OrderBook> secondMarketOrderBookFuture = loadOrderBookTaskFirstExchangeExecutor.submit(getOrderBookCallable(currencyPair, secondExchange));

			loadOrderBookTaskFirstExchangeExecutor.shutdown();
			loadOrderBookTaskFirstExchangeExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
			log.info("Load order books circle end, time {} milliseconds", allStarDate.getTime() - new Date().getTime());

			firtMarketOrderBook = firstMarketOrderBookFuture.get();
			secondMarketOrderBook = secondMarketOrderBookFuture.get();

			List<DepoProfit> firstToSecondDepoProfits = calcAddProfitsList(firtMarketOrderBook, secondMarketOrderBook);
			List<DepoProfit> secondToFirstDepoProfits = calcAddProfitsList(secondMarketOrderBook, firtMarketOrderBook);

			ExecutorService tradesExecutorService = Executors.newFixedThreadPool(2);
			if (firstToSecondDepoProfits.size() > 0) {
				makeTrades(currencyPair, firtsExchange, secondExchange, firstToSecondDepoProfits, tradesExecutorService);

			}
			if (secondToFirstDepoProfits.size() > 0) {
				makeTrades(currencyPair, secondExchange, firtsExchange, secondToFirstDepoProfits, tradesExecutorService);

			}

			tradesExecutorService.shutdown();
			tradesExecutorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			log.info("One circle execution time in milliseconds: {}", new Date().getTime() - allStarDate.getTime());
			Thread.sleep(400);
		}
	}

	private Callable<OrderBook> getOrderBookCallable(CurrencyPair currencyPair, Exchange exchange) {
		return () -> {
			try {
				Date starDate = new Date();
				log.debug("Start load orderBook {}", starDate);
				OrderBook orderBook = exchange.getMarketDataService().getOrderBook(currencyPair, 10);
				log.debug("End load orderBook time in mill {}", starDate.getTime() - new Date().getTime());
				return orderBook;
			} catch (IOException e) {
				log.error(e.getMessage(), e);
				return null;
			}
		};
	}

	private void makeTrades(CurrencyPair currencyPair, Exchange firtsExchange, Exchange secondExchange, List<DepoProfit> depoProfits, ExecutorService executorService) {
		DepoProfit depoProfit = depoProfits.get(0);
		log.info("Found fork by deposit {} and profit {}", depoProfit.getDeposit(), depoProfit.getProfit().multiply(BigDecimal.valueOf(100)));
		if (depoProfit.getProfit().multiply(BigDecimal.valueOf(100)).compareTo(new BigDecimal(MIN_PROFIT_TO_TRADE)) > 0) {
			Callable<Void> tCallable = () -> {
				String uuid = putLimitOrderToBuy(currencyPair, firtsExchange, depoProfit.getSellLimitPrice(), Order.OrderType.BID);
				log.info("Order successfully placed. on exchange {} ID={} and deposit={} and forkProfit={} and type {} and price {}",
						firtsExchange, uuid,
						depoProfit.getDeposit(), depoProfit.getProfit().multiply(BigDecimal.valueOf(100)), Order.OrderType.BID, depoProfit.getSellLimitPrice());
				return null;
			};
			Callable<Void> tCallable2 = () -> {
				String uuid = putLimitOrderToBuy(currencyPair, secondExchange, depoProfit.getBuyLimitPrice(), Order.OrderType.ASK);
				log.info("Order successfully placed. on exchange {} ID={} and deposit={} and forkProfit={} and type {} and price {}",
						secondExchange, uuid,
						depoProfit.getDeposit(), depoProfit.getProfit().multiply(BigDecimal.valueOf(100)), Order.OrderType.ASK, depoProfit.getSellLimitPrice());
				return null;
			};

			executorService.execute(new FutureTask(tCallable));
			executorService.execute(new FutureTask(tCallable2));
		}
	}

	private String putLimitOrderToBuy(CurrencyPair currencyPair, Exchange exchange, BigDecimal limitPrice, Order.OrderType orderType) {
		LimitOrder limitOrder = new LimitOrder.Builder(orderType, currencyPair).limitPrice(limitPrice).originalAmount(BigDecimal.valueOf(coinsAmountForTradeOperation)).build();
		String uuid = null;
		try {
			exchange.getTradeService().placeLimitOrder(limitOrder);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		log.info("Order successfully put. on exchange {} uuid ID={} and deposit={} and type {} and price{}", exchange, uuid, coinsAmountForTradeOperation, orderType, limitPrice);
		return uuid;
	}

	private List<DepoProfit> calcAddProfitsList(OrderBook firstOrderBook, OrderBook secondOrderBook) {
		List<DepoProfit> depositProfitList = new ArrayList<>();
		if(firstOrderBook != null && secondOrderBook != null) {
			List<UniLimitOrder> fistMarketAskOrders = convertToUniLimitOrderListWithCalcSums(firstOrderBook.getAsks(), Order.OrderType.ASK);
			List<UniLimitOrder> secondMarketBidOrders = convertToUniLimitOrderListWithCalcSums(secondOrderBook.getBids(), Order.OrderType.BID);
			/*One way*/
			depositProfitList.add(calculateAddProfitByGlassesByDeposit(fistMarketAskOrders, secondMarketBidOrders, new BigDecimal("0.001")));
			depositProfitList.add(calculateAddProfitByGlassesByDeposit(fistMarketAskOrders, secondMarketBidOrders, new BigDecimal("0.01")));
			/*depositProfitList.add(calculateAddProfitByGlassesByDeposit(fistMarketAskOrders, secondMarketBidOrders, new BigDecimal("0.1")));
			depositProfitList.add(calculateAddProfitByGlassesByDeposit(fistMarketAskOrders, secondMarketBidOrders, new BigDecimal("0.5")));
			depositProfitList.add(calculateAddProfitByGlassesByDeposit(fistMarketAskOrders, secondMarketBidOrders, new BigDecimal("10")));
			depositProfitList.add(calculateAddProfitByGlassesByDeposit(fistMarketAskOrders, secondMarketBidOrders, new BigDecimal("100")));*/


			CollectionUtils.filter(depositProfitList, PredicateUtils.notNullPredicate());
		}

		return depositProfitList;
	}

	public static List<UniLimitOrder> convertToUniLimitOrderListWithCalcSums(List<LimitOrder> orders, Order.OrderType type) {
		Long orderId = 1L;
		BigDecimal originalAmountSum = BigDecimal.ZERO;
		BigDecimal finalSum = BigDecimal.ZERO;
		List<UniLimitOrder> uniOrderList = new ArrayList<>();
		for (LimitOrder askOrBid : orders) {
			originalAmountSum = originalAmountSum.add(askOrBid.getOriginalAmount());
			finalSum = finalSum.add(askOrBid.getLimitPrice().multiply(askOrBid.getOriginalAmount()));
			UniLimitOrder order =
					new UniLimitOrder(orderId, type, askOrBid.getLimitPrice(),
							askOrBid.getOriginalAmount(), originalAmountSum, finalSum);
			uniOrderList.add(order);
			orderId++;
		}
		return uniOrderList;
	}

	private DepoProfit calculateAddProfitByGlassesByDeposit(List<UniLimitOrder> sellUniLimitOrders, List<UniLimitOrder> buyUniLimitOrders, BigDecimal depositAmount) {
		List<UniLimitOrder> sellOrders = sellUniLimitOrders;
		List<UniLimitOrder> buyOrders = buyUniLimitOrders;
		UniLimitOrder sellOrderForCalc = null;
		BigDecimal baseDepositAmount = depositAmount;
		for (UniLimitOrder sellOrder : sellOrders) {
			if (sellOrder.getFinalSum().compareTo(baseDepositAmount) >= 0) {
				sellOrderForCalc = sellOrder;
				break;
			}
		}
		/* not found compatible glass depth wor tge deposit return*/
		if (sellOrderForCalc == null) {
			return null;
		}

		BigDecimal averageSellStackPrice = sellOrderForCalc.getFinalSum().divide(sellOrderForCalc.getOriginalSum(), 12, RoundingMode.HALF_UP);
		BigDecimal coinsForTransferAmount = baseDepositAmount.divide(averageSellStackPrice, 8, RoundingMode.HALF_DOWN);

		UniLimitOrder buyOrderForCalc = null;
		for (UniLimitOrder buyOrder : buyOrders) {
			if (buyOrder.getOriginalSum().compareTo(coinsForTransferAmount) >= 0) {
				buyOrderForCalc = buyOrder;
				break;
			}
		}
		/* not found compatible glass depth wor tge deposit return*/
		if (buyOrderForCalc == null) {
			return null;
		}

		BigDecimal averageBuyStackPrice = buyOrderForCalc.getFinalSum().divide(buyOrderForCalc.getOriginalSum(), 12, RoundingMode.HALF_UP);

		BigDecimal finalCoinsAmount = averageBuyStackPrice.multiply(coinsForTransferAmount);

		BigDecimal depoProfit = finalCoinsAmount.subtract(baseDepositAmount).divide(baseDepositAmount, 4, RoundingMode.HALF_DOWN);
		log.debug("DepoProfit for depo {} is {}", depositAmount, depoProfit.multiply(new BigDecimal("100")));
		if (depoProfit.multiply(BigDecimal.valueOf(100)).compareTo(new BigDecimal(MIN_PROFIT_TO_TRADE)) < 0) {
			return null;
		}

		DepoProfit depositProfit = new DepoProfit();
		depositProfit.setDeposit(baseDepositAmount);
		depositProfit.setAverageSellStackPrice(averageSellStackPrice);
		depositProfit.setAverageBuyStackPrice(averageBuyStackPrice);
		depositProfit.setFinalCoinsAmount(finalCoinsAmount);
		depositProfit.setProfit(depoProfit);
		depositProfit.setSellLimitPrice(sellOrderForCalc.getLimitPrice());
		depositProfit.setSellGlassUpdated(sellOrderForCalc.getTimeStamp());
		depositProfit.setBuyLimitPrice(buyOrderForCalc.getLimitPrice());
		return depositProfit;


	}

	public Exchange getExchange(Class exchangeClass, String api, String secret) {
		ExchangeSpecification exSpec = new ExchangeSpecification(exchangeClass);
		exSpec.setApiKey(api);
		exSpec.setSecretKey(secret);

		return ExchangeFactory.INSTANCE.createExchange(exSpec);
	}

	public Exchange getExchange(Class exchangeClass) {
		ExchangeSpecification exSpec = new ExchangeSpecification(exchangeClass);
		return ExchangeFactory.INSTANCE.createExchange(exSpec);
	}
}
