package ru.fedrbodr.exchangearbitr.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.PredicateUtils;
import org.knowm.xchange.dto.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.MarketPositionFast;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.UniLimitOrder;
import ru.fedrbodr.exchangearbitr.dao.shorttime.repo.LimitOrderRepository;
import ru.fedrbodr.exchangearbitr.dao.shorttime.repo.MarketPositionFastRepositoryCustom;
import ru.fedrbodr.exchangearbitr.dao.shorttime.reports.DepoFork;
import ru.fedrbodr.exchangearbitr.model.DepositProfit;
import ru.fedrbodr.exchangearbitr.model.MarketPositionFastCompare;
import ru.fedrbodr.exchangearbitr.services.MarketPositionFastService;
import ru.fedrbodr.exchangearbitr.utils.SymbolsNamesUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.*;

import static ru.fedrbodr.exchangearbitr.config.CachingConfig.*;

@Service
@Slf4j
public class MarketPositionFastServiceImpl implements MarketPositionFastService {
	@Autowired
	private MarketPositionFastRepositoryCustom marketPositionFastRepositoryCustom;
	@Autowired
	private LimitOrderRepository limitOrderRepository;
	private final int publicPositionsCount = 3;

	@Override
	@Cacheable(TOP_AFTER_10_COMPARE_LIST)
	public List<MarketPositionFastCompare> getTopAfter10MarketPositionFastCompareList() {
		List<MarketPositionFastCompare> topMarketPositionFastCompareList = getTopMarketPositionFastCompareList();
		if (topMarketPositionFastCompareList != null && topMarketPositionFastCompareList.size() > publicPositionsCount) {
			topMarketPositionFastCompareList = topMarketPositionFastCompareList.subList(publicPositionsCount - 1, topMarketPositionFastCompareList.size());
		}
		return topMarketPositionFastCompareList;
	}

	@Override
	@Cacheable(TOP_PROBLEM_AFTER_10_COMPARE_LIST)
	public List<MarketPositionFastCompare> getTopProblemAfterMarketPositionFastCompareList() {
		List<Object[]> topMarketPositionDif = marketPositionFastRepositoryCustom.selectTopProblemMarketPositionFastCompareList();
		return calculateDifferences(topMarketPositionDif);
	}

	@Override
	public List<MarketPositionFastCompare> getTopFullMarketPositionFastCompareList() {
		List<Object[]> topMarketPositionDif = marketPositionFastRepositoryCustom.selectFullMarketPositionFastCompareList();

		return calculateDifferences(topMarketPositionDif);
	}

	@Override
	@Cacheable(TOP_COMPARE_LIST)
	public List<MarketPositionFastCompare> getTopMarketPositionFastCompareList() {
		List<MarketPositionFastCompare> marketPositionFastCompares = getMarketPositionFastCompares();
		for (int i = publicPositionsCount; i < marketPositionFastCompares.size(); i++) {
			marketPositionFastCompares.get(i).setPublicVisible(true);
		}
		if (marketPositionFastCompares.size() > 30) {
			marketPositionFastCompares = marketPositionFastCompares.subList(0, 30);
		}
		return marketPositionFastCompares;
	}

	@Override
	public List<MarketPositionFastCompare> getMarketPositionFastCompares() {
		Date start = new Date();
		log.info("Before selectTopMarketPositionFastCompareList: {}", (new Date().getTime() - start.getTime()) / 1000);
		List<Object[]> topMarketPositionDif = marketPositionFastRepositoryCustom.selectTopMarketPositionFastCompareList();
		List<MarketPositionFastCompare> marketPositionFastCompares = calculateDifferences(topMarketPositionDif);
		log.info("After calculateDifferences(topMarketPositionDif) seconds: {}", (new Date().getTime() - start.getTime()) / 1000);
		return marketPositionFastCompares;
	}

	@Override
	public List<DepoFork> getDepoForks(BigDecimal deposit) {
		Date start = new Date();
		log.info("Before selectTopForksByDeposit(deposit): {} ms", (new Date().getTime() - start.getTime()));
		List<DepoFork> depoForks = marketPositionFastRepositoryCustom.selectTopForksByDeposit(deposit);
		log.info("After selectTopForksByDeposit(deposit) : {} ms", (new Date().getTime() - start.getTime()));
		return depoForks;
	}

	private List<MarketPositionFastCompare> calculateDifferences(List<Object[]> topMarketPositionDif) {
		Date start = new Date();
		List<MarketPositionFastCompare> marketPositionFastCompares = new ArrayList<>();
		log.info("Before topMarketPositionDif.forEach: {}", (new Date().getTime() - start.getTime()) / 1000);

		int nThreads = 3;
		ExecutorService executor = Executors.newFixedThreadPool(nThreads);

		topMarketPositionDif.forEach((Object[] marketPositionDif) -> {
			Callable<Void> tCallable = () -> {
				Date startCalculateMarketPositionFastTime = new Date();
				MarketPositionFastCompare marketPositionFastCompare = calculateMarketPositionFastCompare(marketPositionDif);
				//log.info("After calculateMarketPositionFastCompare: {} ms", new Date().getTime() - startCalculateMarketPositionFastTime.getTime());
				marketPositionFastCompares.add(marketPositionFastCompare);

				return null;
			};
			FutureTask futureTask = new FutureTask(tCallable);
			executor.execute(futureTask);

		});
		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		}
		log.info("After executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);: {} s", (new Date().getTime() - start.getTime()) / 1000);

		if(CollectionUtils.isNotEmpty(marketPositionFastCompares)) {
			Collections.sort(marketPositionFastCompares, (o1, o2) -> {
				if (CollectionUtils.isEmpty(o1.getDepositProfitList()) && CollectionUtils.isEmpty(o2.getDepositProfitList())) {
					return 0;
				}
				if (CollectionUtils.isEmpty(o1.getDepositProfitList()))
					return 1;
				if (CollectionUtils.isEmpty(o2.getDepositProfitList()))
					return -1;

				return o2.getDepositProfitList().get(0).getProfit().compareTo(o1.getDepositProfitList().get(0).getProfit());
			});
		}
		log.info("After calculateDifferences: {}", (new Date().getTime() - start.getTime()) / 1000);
		return marketPositionFastCompares;
	}

	private MarketPositionFastCompare calculateMarketPositionFastCompare(Object[] marketPositionDif) {
		Date calculateMarketPositionFastCompareStartTime = new Date();
		MarketPositionFast marketPositionFast = (MarketPositionFast) marketPositionDif[0];
		MarketPositionFast marketPositionFastToCompare = (MarketPositionFast) marketPositionDif[1];
		MarketPositionFast marketPositionBuy;
		MarketPositionFast marketPositionSell;

		if (marketPositionFast.getAskPrice().compareTo(marketPositionFastToCompare.getBidPrice()) > 1) {
			marketPositionBuy = marketPositionFast;
			marketPositionSell = marketPositionFastToCompare;

		} else {
			marketPositionBuy = marketPositionFastToCompare;
			marketPositionSell = marketPositionFast;
		}

		MarketPositionFastCompare marketPositionFastCompare = new MarketPositionFastCompare(
				marketPositionBuy,
				marketPositionSell,
				(marketPositionSell.getAskPrice().subtract(marketPositionBuy.getBidPrice())).
						divide(marketPositionSell.getAskPrice(), 8, RoundingMode.HALF_DOWN));
		/* TODO can be faster in this place - one request*/
		marketPositionFastCompare.setSellOrders(limitOrderRepository.
				findFirst60ByExchangeMetaAndSymbolAndType(
						marketPositionBuy.getMarketPositionFastPK().getExchangeMeta(),
						marketPositionBuy.getMarketPositionFastPK().getSymbol(),
						Order.OrderType.ASK));
		marketPositionFastCompare.setBuyOrders(limitOrderRepository.
				findFirst60ByExchangeMetaAndSymbolAndType(
						marketPositionSell.getMarketPositionFastPK().getExchangeMeta(),
						marketPositionSell.getMarketPositionFastPK().getSymbol(),
						Order.OrderType.BID));
		// log.info("After set all Orders: {} ms", (new Date().getTime() - startForEach.getTime()));
		calcAddProfitsList(marketPositionFastCompare);
		marketPositionFastCompare.setLieProfitByGlasses(isLieProfitByGlasses(marketPositionFastCompare));
		marketPositionFastCompare.setBuySymbolExchangeUrl(SymbolsNamesUtils.determineUrlToSymbolMarket(marketPositionBuy));
		marketPositionFastCompare.setSellSymbolExchangeUrl(SymbolsNamesUtils.determineUrlToSymbolMarket(marketPositionSell));
		//log.info("After all compiting: {} ms", (new Date().getTime() - calculateMarketPositionFastCompareStartTime.getTime()));
		return marketPositionFastCompare;
	}

	private boolean isLieProfitByGlasses(MarketPositionFastCompare marketPositionFastCompare) {
		boolean isLieProfitByGlasses = true;
		for (DepositProfit depositProfit : marketPositionFastCompare.getDepositProfitList()) {
			if (depositProfit != null && depositProfit.getProfit().compareTo(BigDecimal.ZERO) >= 0) {
				isLieProfitByGlasses = false;
			}
		}
		return isLieProfitByGlasses;
	}

	private void calcAddProfitsList(MarketPositionFastCompare marketPositionFastCompare) {
		if (marketPositionFastCompare.getBuyOrders() != null && marketPositionFastCompare.getSellOrders() != null) {
			List<DepositProfit> depositProfitList = new ArrayList<>();

			depositProfitList.add(calculateAddProfitByGlassesByDeposit(marketPositionFastCompare, new BigDecimal(0.1)));
			depositProfitList.add(calculateAddProfitByGlassesByDeposit(marketPositionFastCompare, new BigDecimal(0.25)));
			depositProfitList.add(calculateAddProfitByGlassesByDeposit(marketPositionFastCompare, new BigDecimal(0.5)));
			depositProfitList.add(calculateAddProfitByGlassesByDeposit(marketPositionFastCompare, new BigDecimal(1)));
			depositProfitList.add(calculateAddProfitByGlassesByDeposit(marketPositionFastCompare, new BigDecimal(2)));
			depositProfitList.add(calculateAddProfitByGlassesByDeposit(marketPositionFastCompare, new BigDecimal(4)));
			depositProfitList.add(calculateAddProfitByGlassesByDeposit(marketPositionFastCompare, new BigDecimal(8)));
			depositProfitList.add(calculateAddProfitByGlassesByDeposit(marketPositionFastCompare, new BigDecimal(12)));
			depositProfitList.add(calculateAddProfitByGlassesByDeposit(marketPositionFastCompare, new BigDecimal(15)));

			CollectionUtils.filter(depositProfitList, PredicateUtils.notNullPredicate());
			marketPositionFastCompare.setDepositProfitList(depositProfitList);
		}
	}

	private DepositProfit calculateAddProfitByGlassesByDeposit(MarketPositionFastCompare marketPositionFastCompare, BigDecimal baseDepositAmount) {
		List<UniLimitOrder> sellOrders = marketPositionFastCompare.getSellOrders();
		List<UniLimitOrder> buyOrders = marketPositionFastCompare.getBuyOrders();

		UniLimitOrder sellOrderForCalc = null;
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

		BigDecimal depoProfit = finalCoinsAmount.subtract(baseDepositAmount).divide(baseDepositAmount, 3, RoundingMode.HALF_DOWN);

		DepositProfit depositProfit = new DepositProfit();
		depositProfit.setDeposit(baseDepositAmount);
		depositProfit.setAverageSellStackPrice(averageSellStackPrice);
		depositProfit.setAverageBuyStackPrice(averageBuyStackPrice);
		depositProfit.setFinalCoinsAmount(finalCoinsAmount);
		depositProfit.setProfit(depoProfit);
		return depositProfit;
	}

	@Override
	public Set<MarketPositionFast> getMarketPositionSetToLoadOrderBooks() {
		Set<MarketPositionFast> marketPositionFasts = new HashSet<>();
		List<Object[]> topMarketPositionDif = marketPositionFastRepositoryCustom.selectTopMarketPositionFastCompareList();
		for (Object[] marketPositionDif : topMarketPositionDif) {
			MarketPositionFast marketPositionFast = (MarketPositionFast) marketPositionDif[0];
			MarketPositionFast marketPositionFastToCompare = (MarketPositionFast) marketPositionDif[1];
			marketPositionFasts.add(marketPositionFast);
			marketPositionFasts.add(marketPositionFastToCompare);
		}
		return marketPositionFasts;
	}
}
