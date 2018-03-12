package ru.fedrbodr.exchangearbitr.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.PredicateUtils;
import org.knowm.xchange.dto.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.DepoProfit;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.ExchangeMetaLong;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.Fork;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.SymbolLong;
import ru.fedrbodr.exchangearbitr.dao.longtime.repo.DepoProfitRepository;
import ru.fedrbodr.exchangearbitr.dao.longtime.repo.ExchangeMetaLongRepository;
import ru.fedrbodr.exchangearbitr.dao.longtime.repo.ForkRepository;
import ru.fedrbodr.exchangearbitr.dao.longtime.repo.ForkRepositoryCustom;
import ru.fedrbodr.exchangearbitr.dao.longtime.reports.ForkInfo;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.Symbol;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.UniLimitOrder;
import ru.fedrbodr.exchangearbitr.dao.shorttime.repo.LimitOrderRepository;
import ru.fedrbodr.exchangearbitr.dao.shorttime.reports.DepoFork;
import ru.fedrbodr.exchangearbitr.services.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static ru.fedrbodr.exchangearbitr.config.CachingConfig.CURRENT_FORKS_CACHE;

@Service
@Slf4j
public class ForkServiceImpl implements ForkService {
	public static final double MIN_PROFIT_TO_LOGGING = 0.003;
	private static final long FORK_DELIMITER_LATENCY_TIME = 30000;
	public static final int FORK_LAST_UPDATED_SECONDS = 100;
	@Autowired
	private MarketPositionFastService marketPositionFastService;
	@Autowired
	private ForkRepository forkRepository;
	@Autowired
	private ForkRepositoryCustom forkRepositoryCustom;
	@Autowired
	private SymbolLongService symbolLongService;
	@Autowired
	private SymbolService symbolService;
	@Autowired
	private ExchangeMetaLongRepository exchangeMetaLongRepository;
	@Autowired
	private ExchangeMetaService exchangeMetaService;
	@Autowired
	private LimitOrderRepository limitOrderRepository;
	@Autowired
	private DepoProfitRepository depoProfitRepository;

	@Cacheable(CURRENT_FORKS_CACHE)
	@Override
	public List<ForkInfo> getCurrentForks() {
		List<ForkInfo> forkInfos = forkRepositoryCustom.selectLatestForksInfo(FORK_LAST_UPDATED_SECONDS);
		for (ForkInfo forkInfo : forkInfos) {
			forkInfo.setProfits(depoProfitRepository.findAllByForkId(forkInfo.getId()));
		}
		if(CollectionUtils.isNotEmpty(forkInfos)) {
			Collections.sort(forkInfos, (o1, o2) -> {
				if (o1.getProfits().size() < 2 && o2.getProfits().size() > 1) {
					return 1;
				}else if (o1.getProfits().size() > 1 && o2.getProfits().size() < 2) {
					return -1;
				}else if(o1.getProfits().size() > 1 && o2.getProfits().size() > 1){
					return -o1.getProfits().get(1).getProfit().compareTo(o2.getProfits().get(1).getProfit());
				}
				return -o1.getProfits().get(0).getProfit().compareTo(o2.getProfits().get(0).getProfit());
			});
		}
		return forkInfos;
	}

	@Override
	public void determineAndPersistForks(long lastOrdersLoadingTime) {
		Date start = new Date();
		//log.info("Before getMarketPositionFastCompares: {}", (new Date().getTime() - start.getTime()) / 1000);
		/*List<MarketPositionFastCompare> marketPositionFastCompares = marketPositionFastService.getMarketPositionFastCompares();*/
		//log.info("After getMarketPositionFastCompares Load seconds: {}", (new Date().getTime() - start.getTime()) / 1000);
		List<DepoFork> depoForks = marketPositionFastService.getDepoForks(BigDecimal.valueOf(0.01));
		log.info("After getDepoForks: {} ms", new Date().getTime() - start.getTime());
		Date currentForkDetectedTime = new Date();
		List<Fork> foundedForks = calcForks(lastOrdersLoadingTime, depoForks, currentForkDetectedTime);
		log.info("After forks for seconds: {}", (new Date().getTime() - start.getTime()) / 1000);
		forkRepositoryCustom.saveAndFlash(foundedForks);
		log.info("After save and flush forks seconds: {}", (new Date().getTime() - start.getTime()) / 1000);
	}

	@Override
	public List<Fork> calcForks(long lastOrdersLoadingTime, List<DepoFork> depoForks, Date currentForkDetectedTime) {
		List<Fork> foundedForks = new ArrayList<>();
		for (DepoFork depoFork : depoForks) {
			Fork fork = new Fork();
			ExchangeMetaLong buyExchangeMeta = exchangeMetaLongRepository.findById(depoFork.getBuyExchangeMetaId());
			fork.setBuyExchangeMeta(buyExchangeMeta);
			ExchangeMetaLong sellExchangeMeta = exchangeMetaLongRepository.findById(depoFork.getSellExchangeMetaId());
			fork.setSellExchangeMeta(sellExchangeMeta);
			// first it is base like BTC seccond alt like
			String[] splitSymbol = depoFork.getSymbolName().split("-");
			SymbolLong symbol = symbolLongService.getOrCreateNewSymbol(splitSymbol[0], splitSymbol[1]);
			fork.setSymbol(symbol);
			ru.fedrbodr.exchangearbitr.dao.longtime.domain.Fork lastSameFork = forkRepository.findFirstByBuyExchangeMetaIdAndSellExchangeMetaIdAndSymbolIdOrderByIdDesc(
					buyExchangeMeta.getId(), sellExchangeMeta.getId(), symbol.getId());
			if (lastSameFork != null) {
				log.debug("LastSameFork is {} and currentForkDetectedTime.getTime() - lastSameFork.getTimestamp().getTime() = {} and lastOrdersLoadingTime+ FORK_DELIMITER_LATENCY_TIME = {}",
						lastSameFork, currentForkDetectedTime.getTime() - lastSameFork.getTimestamp().getTime(), lastOrdersLoadingTime + FORK_DELIMITER_LATENCY_TIME);
				if (currentForkDetectedTime.getTime() - lastSameFork.getTimestamp().getTime() < lastOrdersLoadingTime + FORK_DELIMITER_LATENCY_TIME) {
					fork.setForkWindowId(lastSameFork.getForkWindowId());
				} else {
					/* Previous fork window end and next start with new forkPackId */
					fork.setForkWindowId(lastSameFork.getForkWindowId() + 1);
				}
			} else {
				fork.setForkWindowId((long) 1);
			}

			List<DepoProfit> depoProfits = calcProfitsByOrderBooks(sellExchangeMeta, buyExchangeMeta, symbol);
			depoProfits.forEach(depoProfit -> {
				depoProfit.setFork(fork);
			});
			fork.setProfits(depoProfits);
			fork.setTimestamp(currentForkDetectedTime);

			foundedForks.add(fork);

		}
		return foundedForks;
	}

	private List<DepoProfit> calcProfitsByOrderBooks(ExchangeMetaLong sellExchangeMeta, ExchangeMetaLong buyExchangeMeta, SymbolLong symbol) {
		Symbol symbolShort = symbolService.getOrCreateNewSymbol(symbol.getBaseName(), symbol.getQuoteName());

		List<UniLimitOrder> sellUniLimitOrders = limitOrderRepository.findFirst60ByExchangeMetaAndSymbolAndTypeOrderById(exchangeMetaService.getExchangeMetaById(sellExchangeMeta.getId()),symbolShort,Order.OrderType.ASK);
		List<UniLimitOrder> buyUniLimitOrders = limitOrderRepository.findFirst60ByExchangeMetaAndSymbolAndTypeOrderById(exchangeMetaService.getExchangeMetaById(buyExchangeMeta.getId()),
				symbolShort,
				Order.OrderType.BID);

		return calcAddProfitsList(sellUniLimitOrders, buyUniLimitOrders);
	}

	/**
	 * sellUniLimitOrders - where we buy
	 * buyUniLimitOrders - where we sell
	 */
	private List<DepoProfit> calcAddProfitsList(List<UniLimitOrder> sellUniLimitOrders, List<UniLimitOrder> buyUniLimitOrders) {
		List<DepoProfit> depositProfitList = new ArrayList<>();

		depositProfitList.add(calculateAddProfitByGlassesByDeposit(sellUniLimitOrders, buyUniLimitOrders, new BigDecimal(0.01)));
		depositProfitList.add(calculateAddProfitByGlassesByDeposit(sellUniLimitOrders, buyUniLimitOrders, new BigDecimal(0.1)));
		depositProfitList.add(calculateAddProfitByGlassesByDeposit(sellUniLimitOrders, buyUniLimitOrders, new BigDecimal(0.25)));
		depositProfitList.add(calculateAddProfitByGlassesByDeposit(sellUniLimitOrders, buyUniLimitOrders, new BigDecimal(0.5)));
		depositProfitList.add(calculateAddProfitByGlassesByDeposit(sellUniLimitOrders, buyUniLimitOrders, new BigDecimal(1)));
		depositProfitList.add(calculateAddProfitByGlassesByDeposit(sellUniLimitOrders, buyUniLimitOrders, new BigDecimal(2)));
		depositProfitList.add(calculateAddProfitByGlassesByDeposit(sellUniLimitOrders, buyUniLimitOrders, new BigDecimal(4)));
		depositProfitList.add(calculateAddProfitByGlassesByDeposit(sellUniLimitOrders, buyUniLimitOrders, new BigDecimal(8)));
		depositProfitList.add(calculateAddProfitByGlassesByDeposit(sellUniLimitOrders, buyUniLimitOrders, new BigDecimal(12)));
		depositProfitList.add(calculateAddProfitByGlassesByDeposit(sellUniLimitOrders, buyUniLimitOrders, new BigDecimal(15)));

		CollectionUtils.filter(depositProfitList, PredicateUtils.notNullPredicate());
		return depositProfitList;
	}

	private DepoProfit calculateAddProfitByGlassesByDeposit(List<UniLimitOrder> sellUniLimitOrders, List<UniLimitOrder> buyUniLimitOrders, BigDecimal bigDecimal) {
		List<UniLimitOrder> sellOrders = sellUniLimitOrders;
		List<UniLimitOrder> buyOrders = buyUniLimitOrders;
		UniLimitOrder sellOrderForCalc = null;
		BigDecimal baseDepositAmount = bigDecimal;
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
		if (depoProfit.compareTo(BigDecimal.ZERO) < 0){
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
		depositProfit.setBuyGlassUpdated(buyOrderForCalc.getTimeStamp());
		return depositProfit;


	}


}

