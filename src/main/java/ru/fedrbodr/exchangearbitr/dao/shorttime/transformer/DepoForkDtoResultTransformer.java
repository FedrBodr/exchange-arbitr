package ru.fedrbodr.exchangearbitr.dao.shorttime.transformer;

import org.hibernate.transform.AliasedTupleSubsetResultTransformer;
import ru.fedrbodr.exchangearbitr.dao.shorttime.reports.DepoFork;

import java.math.BigDecimal;

public class DepoForkDtoResultTransformer extends AliasedTupleSubsetResultTransformer {
	public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
		return true;
	}
	@Override
	public Object transformTuple(Object[] tuple, String[] aliases) {
		DepoFork depoFork = new DepoFork();
		depoFork.setDeposit((BigDecimal) tuple[0]);
		depoFork.setSellExchangeMetaId((int) tuple[1]);
		depoFork.setAverageSellStackPrice((BigDecimal) tuple[2]);
		depoFork.setBuyExchangeMetaId((int) tuple[3]);
		depoFork.setAverageBuyStackPrice((BigDecimal) tuple[4]);
		depoFork.setFinalCoinsAmount((BigDecimal) tuple[5]);
		depoFork.setProfit((BigDecimal) tuple[6]);
		depoFork.setSymbolName((String) tuple[7]);
		depoFork.setSellLimitPrice((BigDecimal) tuple[8]);
		depoFork.setBuyLimitPrice((BigDecimal) tuple[9]);
		return depoFork;
	}
}