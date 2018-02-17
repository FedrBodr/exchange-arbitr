package ru.fedrbodr.exchangearbitr.dao.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.fedrbodr.exchangearbitr.dao.MarketPositionFastRepositoryCustom;
import ru.fedrbodr.exchangearbitr.dao.model.MarketPositionFast;

import javax.persistence.EntityManagerFactory;
import java.util.List;

@Repository
public class MarketPositionFastRepositoryCustomImpl implements MarketPositionFastRepositoryCustom {
	@Autowired
	private EntityManagerFactory entityManagerFactory;
	@Override
	@Transactional(readOnly = true)
	public List<Object[]> selectTopAfter10MarketPositionFastCompareList() {
		SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
		Session session = sessionFactory.getCurrentSession();
		return session.createSQLQuery("select {mp1.*}, {mp2.*} from market_position_fast mp1, market_position_fast mp2 " +
				"where mp1.symbol_id = mp2.symbol_id and mp1.exchange_id != mp2.exchange_id and mp1.ask_price > mp2.bid_price and mp2.active=true and mp1.active=true " +
				" order by (mp1.ask_price - mp2.bid_price)/mp1.ask_price desc limit 15,80;")
				.addEntity("mp1", MarketPositionFast.class)
				.addEntity("mp2", MarketPositionFast.class).list();

	}

	@Override
	@Transactional(readOnly = true)
	public List<Object[]> selectTopProblemMarketPositionFastCompareList() {
		SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
		Session session = sessionFactory.getCurrentSession();
		return session.createSQLQuery("select {mp1.*}, {mp2.*} from market_position_fast mp1, market_position_fast mp2 " +
				"where mp1.symbol_id = mp2.symbol_id and mp1.exchange_id != mp2.exchange_id and mp1.ask_price > mp2.bid_price  and (mp2.active=false or mp1.active=false) " +
				" order by (mp1.ask_price - mp2.bid_price)/mp1.ask_price desc limit 20;")
				.addEntity("mp1", MarketPositionFast.class)
				.addEntity("mp2", MarketPositionFast.class).list();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Object[]> selectFullMarketPositionFastCompareList() {
		SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
		Session session = sessionFactory.getCurrentSession();
		return session.createSQLQuery("select {mp1.*}, {mp2.*} from market_position_fast mp1, market_position_fast mp2 " +
				"where mp1.symbol_id = mp2.symbol_id and mp1.exchange_id != mp2.exchange_id and mp1.ask_price > mp2.bid_price and mp2.active=true and mp1.active=true " +
				" order by (mp1.ask_price - mp2.bid_price)/mp1.ask_price desc;")
				.addEntity("mp1", MarketPositionFast.class)
				.addEntity("mp2", MarketPositionFast.class).list();

	}

	@Override
	@Transactional(readOnly = true)
	public List<Object[]> selectTopMarketPositionFastCompareList() {
		SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
		Session session = sessionFactory.getCurrentSession();
		return session.createSQLQuery("select {mp1.*}, {mp2.*} from market_position_fast mp1, market_position_fast mp2 " +
				"where mp1.symbol_id = mp2.symbol_id and mp1.exchange_id != mp2.exchange_id and mp1.ask_price > mp2.bid_price and mp2.active=true and mp1.active=true " +
				" order by mp2.bid_price/mp1.ask_price asc limit 80;")
				.addEntity("mp1", MarketPositionFast.class)
				.addEntity("mp2", MarketPositionFast.class).list();

	}
}
