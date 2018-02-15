package ru.fedrbodr.exchangearbitr.services;

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
import java.util.List;

import static ru.fedrbodr.exchangearbitr.config.CachingConfig.*;

@Service
public class MarketPositionFastServiceImpl implements MarketPositionFastService {
	@Autowired
	private MarketPositionFastRepositoryCustom marketPositionFastRepositoryCustom;
	@Autowired
	private LimitOrderRepository limitOrderRepository;

	@Override
	@Cacheable(TOP_AFTER_10_COMPARE_LIST)
	public List<MarketPositionFastCompare> getTopAfter10MarketPositionFastCompareList() {
		List<Object[]> topMarketPositionDif = marketPositionFastRepositoryCustom.selectTopAfter10MarketPositionFastCompareList();
		return calculateDifferencesForWeb(topMarketPositionDif);
	}

	@Override
	@Cacheable(TOP_PROBLEM_AFTER_10_COMPARE_LIST)
	public List<MarketPositionFastCompare> getTopProblemAfter10MarketPositionFastCompareList() {
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
		return calculateDifferencesForWeb(topMarketPositionDif);
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

			marketPositionFastCompare.setLieProfitByGlasses(LieProfitByGlasses(marketPositionFastCompare));

			marketPositionFastCompare.setBuySymbolExchangeUrl(SymbolsNamesUtils.determineUrlToSymbolMarket(marketPositionBuy));
			marketPositionFastCompare.setSellSymbolExchangeUrl(SymbolsNamesUtils.determineUrlToSymbolMarket(marketPositionSell));

			marketPositionFastCompares.add(marketPositionFastCompare);

		});

		return marketPositionFastCompares;
	}

	private boolean LieProfitByGlasses(MarketPositionFastCompare marketPositionFastCompare) {
		return false;
	}

	private void calcAddProfitsList(MarketPositionFastCompare marketPositionFastCompare) {
		if(marketPositionFastCompare.getBuyOrders() != null && marketPositionFastCompare.getSellOrders() != null) {
			List<DepositProfit> depositProfitList = new ArrayList<>();
			depositProfitList.add(calculateAddProfitByGlassesByDeposit(marketPositionFastCompare, new BigDecimal(0.01)));
			depositProfitList.add(calculateAddProfitByGlassesByDeposit(marketPositionFastCompare, new BigDecimal(0.1)));
			depositProfitList.add(calculateAddProfitByGlassesByDeposit(marketPositionFastCompare, new BigDecimal(0.5)));
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
