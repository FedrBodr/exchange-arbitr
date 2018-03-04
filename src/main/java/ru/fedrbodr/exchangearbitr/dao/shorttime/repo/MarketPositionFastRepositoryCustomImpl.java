package ru.fedrbodr.exchangearbitr.dao.shorttime.repo;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.MarketPositionFast;
import ru.fedrbodr.exchangearbitr.dao.shorttime.reports.DepoFork;
import ru.fedrbodr.exchangearbitr.dao.shorttime.transformer.DepoForkDtoResultTransformer;

import javax.persistence.EntityManagerFactory;
import java.math.BigDecimal;
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
				" order by (mp1.ask_price - mp2.bid_price)/mp1.ask_price desc limit 15,40;")
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
				" order by mp2.bid_price/mp1.ask_price asc limit 250;")
				.addEntity("mp1", MarketPositionFast.class)
				.addEntity("mp2", MarketPositionFast.class).list();

	}

	@Override
	@Transactional(readOnly = true)
	public List<DepoFork> selectTopForksByDeposit(BigDecimal deposit) {
		SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
		String sql = "select\n" +
				":deposit as \"deposit\", \n" +
				"min(sellOrdersCalc.sellOrders_exchange_id) as \"sellExchangeMetaId\",\n" +
				"min(averageSellStackPrice) as \"averageSellStackPrice\",\n" +
				"buyOrders.exchange_id as \"buyExchangeMetaId\",\n" +
				"min(buyOrders.final_sum)/min(buyOrders.original_sum) as \"averageBuyStackPrice\",\n" +
				"min(buyOrders.final_sum)/min(buyOrders.original_sum)*min(coinsForTransferAmount) as \"finalCoinsAmount\",\n" +
				"((min(buyOrders.final_sum)/min(buyOrders.original_sum))*min(coinsForTransferAmount) - :deposit)/:deposit as \"PROFIT\",\n" +
				"min(sellOrdersCalc.name) as \"symbolName\"\n" +
				"from uni_limit_order buyOrders, \n" +
				"(select exchange_id as sellOrders_exchange_id, min(final_sum), :deposit/(min(sellOrders.final_sum)/min(sellOrders.original_sum)) as coinsForTransferAmount, min(sellOrders.final_sum)/min(sellOrders.original_sum) as averageSellStackPrice,\n" +
				"min(s.name) as name, min(s.id) as sy_id\n" +
				"from uni_limit_order sellOrders, symbol s\n" +
				"where s.id=sellOrders.symbol_id and sellOrders.type = 'ASK' and sellOrders.final_sum > :deposit\n" +
				"group by exchange_id, symbol_id) \n" +
				"sellOrdersCalc\n" +
				"where \n" +
				"buyOrders.exchange_id != sellOrdersCalc.sellOrders_exchange_id and buyOrders.type = 'BID'\n" +
				"and buyOrders.original_sum > sellOrdersCalc.coinsForTransferAmount and buyOrders.symbol_id = sellOrdersCalc.sy_id and \n" +
				"((buyOrders.final_sum/buyOrders.original_sum)*coinsForTransferAmount - :deposit)/:deposit > 0.03\n" +
				"group by buyOrders.exchange_id, buyOrders.symbol_id\n" +
				"order by profit desc;";
		Session session = sessionFactory.getCurrentSession();
		SQLQuery sqlQuery = session.createSQLQuery(sql);
		sqlQuery.setParameter("deposit", deposit);
		return session.createSQLQuery(sql).setParameter("deposit", deposit).setResultTransformer(new DepoForkDtoResultTransformer()).list();

	}
}
