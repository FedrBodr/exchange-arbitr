package ru.fedrbodr.exchangearbitr.dao.longtime.repo;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Repository;
import ru.fedrbodr.exchangearbitr.dao.longtime.domain.Fork;
import ru.fedrbodr.exchangearbitr.dao.longtime.reports.ForkInfo;
import ru.fedrbodr.exchangearbitr.dao.longtime.transformer.ForkDtoResultTransformer;

import javax.persistence.EntityManagerFactory;
import java.util.Date;
import java.util.List;

import static ru.fedrbodr.exchangearbitr.config.CachingConfig.CURRENT_FORKS_CACHE;

@Repository
public class ForkRepositoryCustomImpl implements ForkRepositoryCustom {
	@Autowired
	@Qualifier("longTimeEntityManagerFactory")
	private EntityManagerFactory entityManagerFactory;
	@Autowired
	private ExchangeMetaLongRepository exchangeMetaLongRepository;
	@Autowired
	private SymbolLongRepository symbolRepository;
	@Autowired
	private ForkRepository forkRepository;

	/**
	 *  STRON forkLastUpdatedSeconds must be number only do not refactor!!!
	* */
	@Override
	public List<ForkInfo> selectLatestForksInfo(int forkLastUpdatedSeconds) {
		String oldForkLimitInterval = "' "+forkLastUpdatedSeconds+" seconds'";
		SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
		String sql = "select max(fork.id) as id, max(fork_window_id) as forkWindowId, buy_exchange_id as buyExchangeId, sell_exchange_id as sellExchangeId, max(symbol_id) as symbolId, \n" +
				"max(timestamp) as lastUpdatedTime, max(timestamp) as startTime\n" +
				"from fork where timestamp > now() - interval "+oldForkLimitInterval+" \n" +
				"group by buy_exchange_id, sell_exchange_id, symbol_id;";
		Session session = sessionFactory.getCurrentSession();
		SQLQuery sqlQuery = session.createSQLQuery(sql);
		List<ForkInfo> list = sqlQuery.setResultTransformer(new ForkDtoResultTransformer(exchangeMetaLongRepository, symbolRepository)).list();
		for (ForkInfo forkInfo : list) {
			List<Object> objects = forkRepository.selectTimeStampGroupLimit1( forkInfo.getForkWindowId(),
					forkInfo.getSellExchangeMeta().getId(), forkInfo.getBuyExchangeMeta().getId(),
					forkInfo.getSymbol().getId());
			if(objects!=null){
				forkInfo.setStartTime((Date) ((Object[]) objects.get(0))[0]);
			}else{
				forkInfo.setStartTime((Date) ((Object[]) objects.get(0))[0]);
			}
		}
		return list;
	}

	@CacheEvict(allEntries = true, value = {CURRENT_FORKS_CACHE})
	@Override
	public void saveAndFlash(Iterable<? extends Fork> foundedForks) {
		forkRepository.save(foundedForks);
		forkRepository.flush();
	}
}
