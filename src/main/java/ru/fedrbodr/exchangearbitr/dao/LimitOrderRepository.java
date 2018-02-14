package ru.fedrbodr.exchangearbitr.dao;

import org.knowm.xchange.dto.Order;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import ru.fedrbodr.exchangearbitr.dao.model.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.dao.model.SymbolPair;
import ru.fedrbodr.exchangearbitr.dao.model.UniLimitOrder;

import java.util.List;

import static ru.fedrbodr.exchangearbitr.config.CachingConfig.ORDER_LIST_CACHE;

public interface LimitOrderRepository extends JpaRepository<UniLimitOrder, Long>, CrudRepository<UniLimitOrder, Long> {
	@Cacheable(ORDER_LIST_CACHE)
	List<UniLimitOrder> findFirst30ByUniLimitOrderPk_ExchangeMetaAndUniLimitOrderPk_SymbolPairAndUniLimitOrderPk_type(
			ExchangeMeta exchangeMeta, SymbolPair symbolPair, Order.OrderType type);

}