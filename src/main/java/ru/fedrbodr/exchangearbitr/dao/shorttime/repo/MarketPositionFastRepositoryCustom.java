package ru.fedrbodr.exchangearbitr.dao.shorttime.repo;

import org.springframework.transaction.annotation.Transactional;
import ru.fedrbodr.exchangearbitr.dao.shorttime.reports.DepoFork;

import java.math.BigDecimal;
import java.util.List;

public interface MarketPositionFastRepositoryCustom {
	List selectTopAfter10MarketPositionFastCompareList();

	List<Object[]> selectFullMarketPositionFastCompareList();

	List<Object[]> selectTopMarketPositionFastCompareList();

	List<Object[]> selectTopProblemMarketPositionFastCompareList();

	@Transactional(readOnly = true)
	List<DepoFork> selectTopForksByDeposit(BigDecimal deposit);
}
