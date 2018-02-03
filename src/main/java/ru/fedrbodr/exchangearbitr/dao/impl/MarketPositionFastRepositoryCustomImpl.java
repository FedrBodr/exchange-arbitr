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
	public List<Object[]> getTopMarketPositionFastCompareList() {
		SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
		Session session = sessionFactory.getCurrentSession();
		return session.createSQLQuery("select {mp1.*}, {mp2.*} from market_position_fast mp1, market_position_fast mp2 " +
				"where mp1.symbol_id = mp2.symbol_id and mp1.exchange_id != mp2.exchange_id order by abs(mp1.last_price - mp2.last_price) desc limit 20;")
				.addEntity("mp1", MarketPositionFast.class)
				.addEntity("mp2", MarketPositionFast.class).list();

	}
}
