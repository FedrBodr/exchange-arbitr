package ru.fedrbodr.exchangearbitr.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.DepoProfit;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.ExchangeMetaLong;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.Fork;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.SymbolLong;
import ru.fedrbodr.exchangearbitr.dao.longtime.repo.ExchangeMetaLongRepository;
import ru.fedrbodr.exchangearbitr.dao.longtime.repo.ForkRepository;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.Symbol;
import ru.fedrbodr.exchangearbitr.model.MarketPositionFastCompare;
import ru.fedrbodr.exchangearbitr.services.ForkService;
import ru.fedrbodr.exchangearbitr.services.MarketPositionFastService;
import ru.fedrbodr.exchangearbitr.services.SymbolLongService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ForkServiceImpl implements ForkService {
	public static final double MIN_PROFIT_TO_LOGGING = 0.006;
	private static final long FORK_DELIMITER_LATENCY_TIME = 5000;
	@Autowired
	private MarketPositionFastService marketPositionFastService;
	@Autowired
	private ForkRepository forkRepository;
	@Autowired
	private SymbolLongService symbolLongService;
	@Autowired
	private ExchangeMetaLongRepository exchangeMetaLongRepository;

	@Override
	public void determineAndPersistForks(long lastOrdersLoadingTime) {
		List<MarketPositionFastCompare> marketPositionFastCompares = marketPositionFastService.getMarketPositionFastCompares();
		Date currentForkDetectedTime = new Date();
		for (MarketPositionFastCompare marketPositionFastCompare : marketPositionFastCompares) {
			if(marketPositionFastCompare.getDepositProfitList() != null &&
					new BigDecimal(MIN_PROFIT_TO_LOGGING).compareTo(marketPositionFastCompare.getDepositProfitList().get(0).getProfit()) < 0) {
				Fork fork = new Fork();
				ExchangeMetaLong buyExchangeMeta = exchangeMetaLongRepository.findById(marketPositionFastCompare.getSellMarketPosition().getMarketPositionFastPK().getExchangeMeta().getId());
				fork.setBuyExchangeMeta(buyExchangeMeta);
				ExchangeMetaLong sellExchangeMeta = exchangeMetaLongRepository.findById(marketPositionFastCompare.getBuyMarketPosition().getMarketPositionFastPK().getExchangeMeta().getId());
				fork.setSellExchangeMeta(sellExchangeMeta);
				Symbol symbol1 = marketPositionFastCompare.getBuyMarketPosition().getMarketPositionFastPK().getSymbol();
				SymbolLong symbol = symbolLongService.getOrCreateNewSymbol(symbol1.getBaseName(), symbol1.getQuoteName());

				fork.setSymbol(symbol);
				fork.setProfits(DepoProfit.convertToDepoProfitList(marketPositionFastCompare.getDepositProfitList()));
				fork.setTimestamp(currentForkDetectedTime);

				Fork lastSameFork = forkRepository.findFirstByBuyExchangeMetaIdAndSellExchangeMetaIdAndSymbolIdOrderByIdDesc(
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
				}else{
					fork.setForkWindowId((long) 1);
				}
				forkRepository.save(fork);
			}
		}
		forkRepository.flush();
	}
}

