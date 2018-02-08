package ru.fedrbodr.exchangearbitr.dao.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.fedrbodr.exchangearbitr.dao.MarketPositionFastRepositoryCustom;
import ru.fedrbodr.exchangearbitr.model.dao.MarketPositionFast;

import javax.persistence.EntityManagerFactory;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class MarketPositionFastRepositoryCustomImpl implements MarketPositionFastRepositoryCustom {
	@Autowired
	private EntityManagerFactory entityManagerFactory;
	@Override
	@Transactional
	public List<Object[]> getTopAfter12MarketPositionFastCompareList() {
		SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
		Session session = sessionFactory.getCurrentSession();
		return session.createSQLQuery("select {mp1.*}, {mp2.*} from market_position_fast mp1, market_position_fast mp2 " +
				"where mp1.symbol_id = mp2.symbol_id and mp1.exchange_id != mp2.exchange_id and mp1.last_price > mp2.last_price " +
				" order by mp2.last_price/mp1.last_price asc limit 12,30;")
				.addEntity("mp1", MarketPositionFast.class)
				.addEntity("mp2", MarketPositionFast.class).list();

	}
	@Override
	@Transactional
	public List<Object[]> getTopFullMarketPositionFastCompareList() {
		SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
		Session session = sessionFactory.getCurrentSession();
		return session.createSQLQuery("select {mp1.*}, {mp2.*} from market_position_fast mp1, market_position_fast mp2 " +
				"where mp1.symbol_id = mp2.symbol_id and mp1.exchange_id != mp2.exchange_id and mp1.last_price > mp2.last_price " +
				" order by mp2.last_price/mp1.last_price asc;")
				.addEntity("mp1", MarketPositionFast.class)
				.addEntity("mp2", MarketPositionFast.class).list();

	}

	@Override
	@Transactional
	public List<Object[]> getTopProblemMarketPositionFastCompareList() {
		SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
		Session session = sessionFactory.getCurrentSession();
		return session.createSQLQuery("select {mp1.*}, {mp2.*} from market_position_fast mp1, market_position_fast mp2 " +
				"where mp1.symbol_id = mp2.symbol_id and mp1.exchange_id != mp2.exchange_id and mp1.last_price > mp2.last_price " +
				" order by mp2.last_price/mp1.last_price asc limit 0,8;")
				.addEntity("mp1", MarketPositionFast.class)
				.addEntity("mp2", MarketPositionFast.class).list();

	}
}
