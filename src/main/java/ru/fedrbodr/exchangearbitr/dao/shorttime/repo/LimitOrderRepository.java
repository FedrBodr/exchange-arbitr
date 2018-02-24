package ru.fedrbodr.exchangearbitr.dao.shorttime.repo;

import org.knowm.xchange.dto.Order;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.ExchangeMeta;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.Symbol;
import ru.fedrbodr.exchangearbitr.dao.shorttime.domain.UniLimitOrder;

import java.util.List;

import static ru.fedrbodr.exchangearbitr.config.CachingConfig.ORDER_LIST_CACHE;

public interface LimitOrderRepository extends JpaRepository<UniLimitOrder, Long>, CrudRepository<UniLimitOrder, Long> {
	@Cacheable(ORDER_LIST_CACHE)
	List<UniLimitOrder> findFirst30ByUniLimitOrderPk_ExchangeMetaAndUniLimitOrderPk_SymbolAndUniLimitOrderPk_type(
			ExchangeMeta exchangeMeta, Symbol symbol, Order.OrderType type);

	void deleteByUniLimitOrderPk_ExchangeMetaAndUniLimitOrderPk_Symbol(
			ExchangeMeta exchangeMeta, Symbol symbol);

}
