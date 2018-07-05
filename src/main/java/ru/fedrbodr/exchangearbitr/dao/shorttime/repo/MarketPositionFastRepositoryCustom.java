package ru.fedrbodr.exchangearbitr.dao.shorttime.repo;

import org.springframework.transaction.annotation.Transactional;
import ru.fedrbodr.exchangearbitr.dao.shorttime.reports.DepoFork;

import java.math.BigDecimal;
import java.util.List;

public interface MarketPositionFastRepositoryCustom {
	@Transactional(readOnly = true)
	List<Object[]> selectTopProblemMarketPositionFastCompareList();

	List<Object[]> selectFullMarketPositionFastCompareList();

	List<Object[]> selectTopMarketPositionFastCompareList();

	@Transactional(readOnly = true)
	List<Object[]> selectTopAfter10MarketPositionFastCompareList();

	@Transactional(readOnly = true)
	List<DepoFork> selectTopForksByDeposit(BigDecimal deposit);
}
