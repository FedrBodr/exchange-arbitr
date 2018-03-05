package ru.fedrbodr.exchangearbitr.dao.longtime.repo;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.fedrbodr.exchangearbitr.dao.longtime.reports.ForkInfo;
import ru.fedrbodr.exchangearbitr.dao.longtime.transformer.ForkDtoResultTransformer;

import javax.persistence.EntityManagerFactory;
import java.util.List;

@Repository
public class ForkRepositoryCustomImpl implements ForkRepositoryCustom {
	@Autowired
	@Qualifier("longTimeEntityManagerFactory")
	private EntityManagerFactory entityManagerFactory;
	@Autowired
	private ExchangeMetaLongRepository exchangeMetaLongRepository;
	@Autowired
	private SymbolLongRepository symbolRepository;

	/**
	 *  STRON forkLastUpdatedSeconds must be number only do not refactor!!!
	* */
	@Override
	public List<ForkInfo> selectLatestForksInfo(int forkLastUpdatedSeconds) {
		String oldForkLimitInterval = "' "+forkLastUpdatedSeconds+" seconds'";
		SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
		String sql = "select forks.fork_id as id, forks.fork_window_id as forkWindowId, forks.buy_exchange_id as buyExchangeId, \n" +
				"forks.sell_exchange_id as sellExchangeId, symbol_id as symbolId, startTime as startTime, lastUpdatedTime as lastUpdatedTime from (\n" +
				"select max(fork.id) as fork_id, max(fork_window_id) as fork_window_id, buy_exchange_id, sell_exchange_id, symbol_id, \n" +
				"max(timestamp) as lastUpdatedTime, min(timestamp) as startTime\n" +
				"from fork \n" +
				"group by buy_exchange_id, sell_exchange_id, symbol_id) forks\n" +
				"where forks.lastUpdatedTime > now() - interval "+oldForkLimitInterval+";";
		Session session = sessionFactory.getCurrentSession();
		SQLQuery sqlQuery = session.createSQLQuery(sql);
		return sqlQuery.setResultTransformer(new ForkDtoResultTransformer(exchangeMetaLongRepository, symbolRepository)).list();
	}
}
