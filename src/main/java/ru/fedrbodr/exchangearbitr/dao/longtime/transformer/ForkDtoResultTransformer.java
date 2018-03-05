package ru.fedrbodr.exchangearbitr.dao.longtime.transformer;

import org.hibernate.transform.AliasedTupleSubsetResultTransformer;
import ru.fedrbodr.exchangearbitr.dao.longtime.repo.ExchangeMetaLongRepository;
import ru.fedrbodr.exchangearbitr.dao.longtime.repo.SymbolLongRepository;
import ru.fedrbodr.exchangearbitr.dao.longtime.reports.ForkInfo;

import java.math.BigInteger;
import java.util.Date;

public class ForkDtoResultTransformer extends AliasedTupleSubsetResultTransformer {

	private final ExchangeMetaLongRepository exchangeMetaLongService;
	private final SymbolLongRepository symbolRepository;

	public ForkDtoResultTransformer(ExchangeMetaLongRepository exchangeMetaLongService, SymbolLongRepository symbolRepository) {
		this.exchangeMetaLongService = exchangeMetaLongService;
		this.symbolRepository = symbolRepository;
	}

	public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
		return true;
	}
	@Override
	public Object transformTuple(Object[] tuple, String[] aliases) {
		ForkInfo fork = new ForkInfo();
		fork.setId(((BigInteger) tuple[0]).longValue());
		fork.setForkWindowId(((BigInteger) tuple[1]).longValue());
		fork.setBuyExchangeMeta(exchangeMetaLongService.findById((Integer) tuple[2]));
		fork.setSellExchangeMeta(exchangeMetaLongService.findById((Integer) tuple[3]));
		fork.setSymbol(symbolRepository.findById(((BigInteger) tuple[4]).longValue()));
		fork.setStartTime((Date) tuple[5]);
		fork.setLastUpdatedTime((Date) tuple[6]);

		return fork;
	}
}