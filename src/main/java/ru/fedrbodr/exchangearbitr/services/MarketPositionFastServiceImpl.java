package ru.fedrbodr.exchangearbitr.services;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.PredicateUtils;
import org.knowm.xchange.dto.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.dao.LimitOrderRepository;
import ru.fedrbodr.exchangearbitr.dao.MarketPositionFastRepositoryCustom;
import ru.fedrbodr.exchangearbitr.dao.model.MarketPositionFast;
import ru.fedrbodr.exchangearbitr.dao.model.UniLimitOrder;
import ru.fedrbodr.exchangearbitr.model.DepositProfit;
import ru.fedrbodr.exchangearbitr.model.MarketPositionFastCompare;
import ru.fedrbodr.exchangearbitr.utils.SymbolsNamesUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ru.fedrbodr.exchangearbitr.config.CachingConfig.*;

@Service
public class MarketPositionFastServiceImpl implements MarketPositionFastService {
	@Autowired
	private MarketPositionFastRepositoryCustom marketPositionFastRepositoryCustom;
	@Autowired
	private LimitOrderRepository limitOrderRepository;
	private final int publicPositionsCount = 10;

	@Override
	@Cacheable(TOP_AFTER_10_COMPARE_LIST)
	public List<MarketPositionFastCompare> getTopAfter10MarketPositionFastCompareList() {
		List<MarketPositionFastCompare> topMarketPositionFastCompareList = getTopMarketPositionFastCompareList();
		return topMarketPositionFastCompareList.subList(publicPositionsCount-1, topMarketPositionFastCompareList.size());
	}

	@Override
	@Cacheable(TOP_PROBLEM_AFTER_10_COMPARE_LIST)
	public List<MarketPositionFastCompare> getTopProblemAfterMarketPositionFastCompareList() {
		List<Object[]> topMarketPositionDif = marketPositionFastRepositoryCustom.selectTopProblemMarketPositionFastCompareList();
		return calculateDifferencesForWeb(topMarketPositionDif);
	}

	@Override
	public List<MarketPositionFastCompare> getTopFullMarketPositionFastCompareList() {
		List<Object[]> topMarketPositionDif = marketPositionFastRepositoryCustom.selectFullMarketPositionFastCompareList();

		return calculateDifferencesForWeb(topMarketPositionDif);
	}

	@Override
	@Cacheable(TOP_COMPARE_LIST)
	public List<MarketPositionFastCompare> getTopMarketPositionFastCompareList() {
		List<Object[]> topMarketPositionDif = marketPositionFastRepositoryCustom.selectTopMarketPositionFastCompareList();
		List<MarketPositionFastCompare> marketPositionFastCompares = calculateDifferencesForWeb(topMarketPositionDif);
		for (int i = publicPositionsCount; i <marketPositionFastCompares.size() ; i++) {
			marketPositionFastCompares.get(i).setPublicVisible(true);
		}
		return marketPositionFastCompares;
	}

	private List<MarketPositionFastCompare> calculateDifferencesForWeb(List<Object[]> topMarketPositionDif) {
		List<MarketPositionFastCompare> marketPositionFastCompares = new ArrayList<>();

		topMarketPositionDif.forEach(marketPositionDif -> {

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
			MarketPositionFastCompare marketPositionFastCompare = new MarketPositionFastCompare(
					marketPositionBuy,
					marketPositionSell,
					(marketPositionSell.getAskPrice().subtract(marketPositionBuy.getBidPrice())).
							divide(marketPositionSell.getAskPrice(), 8, RoundingMode.HALF_DOWN));

			marketPositionFastCompare.setSellOrders(limitOrderRepository.
					findFirst30ByUniLimitOrderPk_ExchangeMetaAndUniLimitOrderPk_SymbolPairAndUniLimitOrderPk_type(
							marketPositionBuy.getMarketPositionFastPK().getExchangeMeta(),
							marketPositionBuy.getMarketPositionFastPK().getSymbolPair(),
							Order.OrderType.ASK));

			marketPositionFastCompare.setBuyOrders(limitOrderRepository.
					findFirst30ByUniLimitOrderPk_ExchangeMetaAndUniLimitOrderPk_SymbolPairAndUniLimitOrderPk_type(
							marketPositionSell.getMarketPositionFastPK().getExchangeMeta(),
							marketPositionSell.getMarketPositionFastPK().getSymbolPair(),
							Order.OrderType.BID));

			calcAddProfitsList(marketPositionFastCompare);

			marketPositionFastCompare.setLieProfitByGlasses(isLieProfitByGlasses(marketPositionFastCompare));

			marketPositionFastCompare.setBuySymbolExchangeUrl(SymbolsNamesUtils.determineUrlToSymbolMarket(marketPositionBuy));
			marketPositionFastCompare.setSellSymbolExchangeUrl(SymbolsNamesUtils.determineUrlToSymbolMarket(marketPositionSell));

			marketPositionFastCompares.add(marketPositionFastCompare);

		});
// || o1.getDepositProfitList().get(0) == null && o2.getDepositProfitList().get(0)==null
		Collections.sort(marketPositionFastCompares, (o1, o2) -> {
			if(CollectionUtils.isEmpty(o1.getDepositProfitList()) && CollectionUtils.isEmpty(o2.getDepositProfitList())) {
				return 0;
			}
			if(CollectionUtils.isEmpty(o1.getDepositProfitList()))
				return 1;
			if(CollectionUtils.isEmpty(o2.getDepositProfitList()))
				return -1;

			return o2.getDepositProfitList().get(0).getProfit().compareTo(o1.getDepositProfitList().get(0).getProfit());
		});

		return marketPositionFastCompares;
	}

	private boolean isLieProfitByGlasses(MarketPositionFastCompare marketPositionFastCompare) {
		boolean isLieProfitByGlasses = true;
		for (DepositProfit depositProfit : marketPositionFastCompare.getDepositProfitList()) {
			if(depositProfit != null && depositProfit.getProfit().compareTo(BigDecimal.ZERO)>=0){
				isLieProfitByGlasses = false;
			}
		}
		return isLieProfitByGlasses;
	}

	private void calcAddProfitsList(MarketPositionFastCompare marketPositionFastCompare) {
		if(marketPositionFastCompare.getBuyOrders() != null && marketPositionFastCompare.getSellOrders() != null) {
			List<DepositProfit> depositProfitList = new ArrayList<>();

			depositProfitList.add(calculateAddProfitByGlassesByDeposit(marketPositionFastCompare, new BigDecimal(0.1)));
			depositProfitList.add(calculateAddProfitByGlassesByDeposit(marketPositionFastCompare, new BigDecimal(0.25)));
			depositProfitList.add(calculateAddProfitByGlassesByDeposit(marketPositionFastCompare, new BigDecimal(0.5)));
			depositProfitList.add(calculateAddProfitByGlassesByDeposit(marketPositionFastCompare, new BigDecimal(2)));
			depositProfitList.add(calculateAddProfitByGlassesByDeposit(marketPositionFastCompare, new BigDecimal(8)));
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
			if(sellOrder.getFinalSum().compareTo(baseDepositAmount) >= 0 ){
				sellOrderForCalc = sellOrder;
				break;
			}
		}
		/* not found compatible glass depth wor tge deposit return*/
		if(sellOrderForCalc == null){
			return null;
		}

		BigDecimal averageSellStackPrice = sellOrderForCalc.getFinalSum().divide(sellOrderForCalc.getOriginalSum(),12, RoundingMode.HALF_UP);
		BigDecimal coinsForTransferAmount = baseDepositAmount.divide(averageSellStackPrice, 8, RoundingMode.HALF_DOWN);

		UniLimitOrder buyOrderForCalc = null;
		for (UniLimitOrder buyOrder : buyOrders) {
			if(buyOrder.getOriginalSum().compareTo(coinsForTransferAmount) >= 0 ){
				buyOrderForCalc = buyOrder;
				break;
			}
		}
		/* not found compatible glass depth wor tge deposit return*/
		if(buyOrderForCalc == null){
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


}
